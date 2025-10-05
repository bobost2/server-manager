package com.bobost.panel_back_end.service.impl;

import com.bobost.panel_back_end.data.entity.ServerInstance;
import com.bobost.panel_back_end.data.repository.ServerInstanceRepository;
import com.bobost.panel_back_end.service.InstanceService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
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
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class InstanceServiceImpl implements InstanceService {
    private final DockerClient dockerClient;
    private final ServerInstanceRepository instanceRepo;

    public InstanceServiceImpl(DockerClient dockerClient, ServerInstanceRepository serverInstanceRepository) {
        this.dockerClient = dockerClient;
        this.instanceRepo = serverInstanceRepository;
    }

    @Override
    public ServerInstance createInstance(String name) {
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

        CreateContainerResponse resp = dockerClient.createContainerCmd("bobost2/minecraft-server-instance:alpha-0.2")
                .withName(name)
                .withExposedPorts(ExposedPort.tcp(25565))
                .withHostConfig(HostConfig.newHostConfig()
                        .withPortBindings(new PortBinding(
                                Ports.Binding.bindPort(25565),
                                new ExposedPort(25565)
                        ))
                )
                .withEnv("JWT_SECRET=" + base64Secret)
                .exec();

        ServerInstance inst = new ServerInstance();
        inst.setName(name);
        inst.setContainerId(resp.getId());
        inst.setJwtSecret(base64Secret);
        inst.setStatus("CREATED");
        instanceRepo.save(inst);

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
        inst.setStatus("RUNNING");
        return instanceRepo.save(inst);
    }

    @Override
    public List<ServerInstance> listInstances() {
        // you can also refresh status via docker.inspectContainerCmd(...)
        return instanceRepo.findAll();
    }

    @Override
    public void stopInstance(Long id) {
        ServerInstance inst = instanceRepo.findById(id).orElseThrow();
        dockerClient.stopContainerCmd(inst.getContainerId()).exec();
        inst.setStatus("STOPPED");
        instanceRepo.save(inst);
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
}