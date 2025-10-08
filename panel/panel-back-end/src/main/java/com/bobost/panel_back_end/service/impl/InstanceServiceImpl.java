package com.bobost.panel_back_end.service.impl;

import com.bobost.panel_back_end.data.entity.AllocatedPorts;
import com.bobost.panel_back_end.data.entity.ServerInstance;
import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.AllocatedPortsRepository;
import com.bobost.panel_back_end.data.repository.ServerInstanceRepository;
import com.bobost.panel_back_end.data.repository.UserRepository;
import com.bobost.panel_back_end.dto.instance.management.CreateInstanceDTO;
import com.bobost.panel_back_end.dto.instance.management.InstanceStatsDTO;
import com.bobost.panel_back_end.service.InstanceService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class InstanceServiceImpl implements InstanceService {
    private final DockerClient dockerClient;
    private final ServerInstanceRepository instanceRepo;
    private final UserRepository userRepo;
    private final AllocatedPortsRepository allocatedPortsRepo;

    public InstanceServiceImpl(DockerClient dockerClient, ServerInstanceRepository serverInstanceRepository,
                               UserRepository userRepository, AllocatedPortsRepository allocatedPortsRepo) {
        this.dockerClient = dockerClient;
        this.instanceRepo = serverInstanceRepository;
        this.userRepo = userRepository;
        this.allocatedPortsRepo = allocatedPortsRepo;
    }

    @Override
    public long createInstance(long adminUserId, CreateInstanceDTO createInstanceDTO) {

        if (!isPortValid(createInstanceDTO.getPort())) {
            throw new IllegalArgumentException("Port is already in use or outside of the valid range.");
        }

        boolean isUserListNotEmpty = createInstanceDTO.getUserOwnerIds() != null && !createInstanceDTO.getUserOwnerIds().isEmpty();
        if (isUserListNotEmpty && !areUserIdsValid(createInstanceDTO.getUserOwnerIds())) {
            throw new IllegalArgumentException("One or more user IDs are invalid.");
        }

        String internalName = generateNameFromFriendlyName(createInstanceDTO.getName());

        OctetSequenceKey jwk;
        try {
            jwk = new OctetSequenceKeyGenerator(256)
                    .algorithm(JWSAlgorithm.HS256)
                    .generate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWK", e);
        }

        SecretKey secretKey = jwk.toSecretKey();
        String base64Secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        HostConfig hostConfig = new HostConfig()
                .withPortBindings(new PortBinding(
                        Ports.Binding.bindPort(createInstanceDTO.getPort()),
                        new ExposedPort(25565)
                ))
                .withRestartPolicy(RestartPolicy.unlessStoppedRestart());

        if (createInstanceDTO.getLimits() != null) {
            if (createInstanceDTO.getLimits().isLimitCPU()) {
                long nanoCpus = (long)(createInstanceDTO.getLimits().getAmountOfCPUs() * 1_000_000_000L);
                hostConfig.withNanoCPUs(nanoCpus);
            }

            if (createInstanceDTO.getLimits().isLimitMemory()) {
                long memoryBytes = createInstanceDTO.getLimits().getMemoryLimitMB() * 1024L * 1024L;
                hostConfig.withMemory(memoryBytes);
            }
        }

        String volumeName = internalName + "_volume";
        try {
            dockerClient.createVolumeCmd()
                    .withName(volumeName)
                    .exec();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Docker volume", e);
        }

        hostConfig.withMounts(List.of(
                new Mount().withType(MountType.VOLUME).withSource(volumeName).withTarget("/app/data")
        ));

        CreateContainerResponse resp = dockerClient.createContainerCmd("bobost2/minecraft-server-instance:alpha-0.2")
                .withName(internalName)
                .withExposedPorts(ExposedPort.tcp(25565))
                .withHostConfig(hostConfig)
                .withEnv("JWT_SECRET=" + base64Secret)
                .exec();

        ServerInstance inst = new ServerInstance();
        inst.setName(internalName);
        inst.setFriendlyName(createInstanceDTO.getName());
        inst.setContainerId(resp.getId());
        inst.setJwtSecret(base64Secret);
        inst.setPort(createInstanceDTO.getPort());

        try{
            dockerClient.createNetworkCmd()
                    .withName("management_net")
                    .exec();
        } catch (Exception e){
            System.out.println("Network management_net already exists, skipping creation.");
        }

        dockerClient.connectToNetworkCmd()
                .withContainerId(resp.getId())
                .withNetworkId("management_net")
                .exec();

        dockerClient.startContainerCmd(resp.getId()).exec();
        instanceRepo.save(inst);

        if (isUserListNotEmpty) {
            List<User> users = userRepo.findAllById(createInstanceDTO.getUserOwnerIds());
            for (User user : users) {
                user.getServerInstances().add(inst);
            }
            userRepo.saveAll(users);
        }

        return inst.getId();
    }

    @Override
    public List<ServerInstance> listInstances() {
        // you can also refresh status via docker.inspectContainerCmd(...)
        return instanceRepo.findAll();
    }

    @Override
    public void startInstance(Long id) {
        ServerInstance inst = instanceRepo.findById(id).orElseThrow();

        InspectContainerResponse i = dockerClient.inspectContainerCmd(inst.getContainerId()).exec();
        if (Boolean.TRUE.equals(i.getState().getRunning())) {
            throw new IllegalStateException("Instance is already running.");
        }

        dockerClient.startContainerCmd(inst.getContainerId()).exec();
        instanceRepo.save(inst);
    }

    @Override
    public void stopInstance(Long id) {
        ServerInstance inst = instanceRepo.findById(id).orElseThrow();

        InspectContainerResponse i = dockerClient.inspectContainerCmd(inst.getContainerId()).exec();
        if (!Boolean.TRUE.equals(i.getState().getRunning())) {
            throw new IllegalStateException("Instance is already stopped.");
        }

        dockerClient.stopContainerCmd(inst.getContainerId()).exec();
        instanceRepo.save(inst);
    }

    @Override
    public void deleteInstance(Long id) {
        ServerInstance inst = instanceRepo.findById(id).orElseThrow();

        List<User> users = userRepo.findAllByServerInstancesContains(inst);
        if (!users.isEmpty()) {
            for (User user : users) {
                user.getServerInstances().remove(inst);
            }
            userRepo.saveAll(users);
        }

        List<AllocatedPorts> allocatedPorts = allocatedPortsRepo.findAllByServerInstance(inst);
        if (!allocatedPorts.isEmpty()) {
            allocatedPortsRepo.deleteAll(allocatedPorts);
        }

        try {
            dockerClient.removeContainerCmd(inst.getContainerId())
                    .withForce(true)
                    .withRemoveVolumes(true)
                    .exec();
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove Docker container", e);
        }

        String volumeName = inst.getName() + "_volume";
        try {
            dockerClient.removeVolumeCmd(volumeName).exec();
        } catch (Exception e) {
            System.out.println("Failed to remove Docker volume: " + e.getMessage());
        }

        instanceRepo.delete(inst);
    }

    @Override
    public InstanceStatsDTO getInstanceStats(Long id) {
        InstanceStatsDTO dto = new InstanceStatsDTO();
        ServerInstance inst = instanceRepo.findById(id).orElseThrow();
        dto.setId(inst.getId());
        dto.setFriendlyName(inst.getFriendlyName());

        InspectContainerResponse i = dockerClient.inspectContainerCmd(inst.getContainerId()).exec();
        dto.setRunning(Boolean.TRUE.equals(i.getState().getRunning()));
        return dto;
    }

    @Override
    public List<InstanceStatsDTO> getAllInstances(Long userId) {
        List<InstanceStatsDTO> instanceList = new ArrayList<>();
        List<ServerInstance> instances;
        User user = userRepo.findById(userId).orElseThrow();

        if (user.isAdmin()) {
            instances = instanceRepo.findAll();
        } else {
            instances = new ArrayList<>(user.getServerInstances());
        }

        for (ServerInstance inst : instances) {
            InstanceStatsDTO dto = new InstanceStatsDTO();
            dto.setId(inst.getId());
            dto.setFriendlyName(inst.getFriendlyName());

            InspectContainerResponse i = dockerClient.inspectContainerCmd(inst.getContainerId()).exec();
            dto.setRunning(Boolean.TRUE.equals(i.getState().getRunning()));
            instanceList.add(dto);
        }

        return instanceList;
    }

    @Override
    public String returnInstanceJavaVersion(Long id) {
        ServerInstance inst = instanceRepo.findAll().getFirst();

        String base64Secret = inst.getJwtSecret();
        SecretKeySpec key = new SecretKeySpec(base64Secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("panel")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(60)))
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claims);

        try {
            signedJWT.sign(new MACSigner(key));
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign JWT", e);
        }
        String token = signedJWT.serialize();

        String url = "http://" + inst.getName() + ":8080/java-versions/selected";
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        var test = new org.springframework.web.client.RestTemplate()
                .exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
        var body2 = test.getBody();
        if (body2 != null) {
            return body2;
        }
        return "";
    }

    @Override
    public boolean isPortValid(int port) {
        // TODO: Implement range check from config file.

        // Check if port is registered in DB already
        var instanceWithPort = instanceRepo.findByPort(port);
        if (instanceWithPort != null && instanceWithPort.isPresent()) {
            return false;
        }

        // Check the other allocated ports
        var allocatedPort = instanceRepo.findByPort(port);
        if (allocatedPort != null && allocatedPort.isPresent()) {
            return false;
        }

        // Check if other process is using the port by trying to create a dummy socket.
        try (ServerSocket socket = new ServerSocket()) {
            socket.setReuseAddress(false);
            socket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public int getNextAvailablePort() {
        return 0;
    }

    @Override
    public String generateNameFromFriendlyName(String friendlyName) {
        // Check if friendly name contains docker invalid characters (valid: a-z, A-Z, 0-9, ., _, -)
        if (!friendlyName.matches("[a-zA-Z0-9._\\- ]+")) {
            throw new IllegalArgumentException("Name contains illegal characters.");
        }

        // Replace all uppercase letters with lowercase letters with space like (TestTest -> test-test)
        String finalName = friendlyName.replaceAll("([a-z0-9])([A-Z])", "$1 $2");
        finalName = finalName.toLowerCase();
        finalName = finalName.replaceAll(" ", "-");

        // Check if name is already taken
        if (instanceRepo.findByName(finalName).isPresent()) {
            // find if there is a number at the end of the name like name-1, name-2 and increment it
            var instancesWithSameName = instanceRepo.findAllByNameStartingWith(finalName);

            //noinspection ExtractMethodRecommender
            int max = 1;
            for (var inst : instancesWithSameName) {
                String name = inst.getName();
                if (name.equals(finalName)) {
                    continue;
                }
                String[] parts = name.split("-");
                try {
                    int num = Integer.parseInt(parts[parts.length - 1]);
                    if (num >= max) {
                        max = num + 1;
                    }
                } catch (NumberFormatException e) {
                    // Not a number, ignore
                }
            }
            finalName = finalName + "-" + max;
        }

        return finalName;
    }

    @Override
    public boolean areUserIdsValid(List<Long> userIds) {
        for (Long id : userIds) {
            if (userRepo.findById(id).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}