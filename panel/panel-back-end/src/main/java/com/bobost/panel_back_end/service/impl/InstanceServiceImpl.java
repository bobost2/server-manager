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
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;

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
                .withNetworkMode("management_net")
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

        dockerClient.startContainerCmd(resp.getId()).exec();
        inst.setStatus("RUNNING");
        return instanceRepo.save(inst);
    }
}
