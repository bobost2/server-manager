package com.bobost.server_instance.service.impl;

import com.bobost.server_instance.data.entity.InstanceConfig;
import com.bobost.server_instance.data.repository.InstanceConfigRepository;
import com.bobost.server_instance.model.MinecraftVersionType;
import com.bobost.server_instance.service.LifecycleService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class LifecycleServiceImpl implements LifecycleService {

    InstanceConfigRepository instanceConfigRepository;

    public LifecycleServiceImpl(InstanceConfigRepository instanceConfigRepository) {
        this.instanceConfigRepository = instanceConfigRepository;
    }

    @Override
    public void onStartup() {
        System.out.println("[!] Initializing instance...");

        long maxMemoryInMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        System.out.println("[!] Allocated size is " + maxMemoryInMB + "MB");

        // Init default instance config
        if ( instanceConfigRepository.count() == 0 ) {
            System.out.println("[!] Creating default instance config...");
            InstanceConfig instanceConfig = new InstanceConfig(
                    21,
                    MinecraftVersionType.NONE,
                    ""
            );
            instanceConfigRepository.save(instanceConfig);
        }

        System.out.println("[!] Loading instance config...");
        InstanceConfig instanceConfig = instanceConfigRepository.findAll().getFirst();

        if (instanceConfig.getSelectedMinecraftVersionType() == MinecraftVersionType.NONE) {
            System.out.println("[!] Trying to load server jar...");
            runServer();
        } else {
            System.out.println("[!!] No version selected, manual setup required");
        }
    }

    @Override
    public boolean runServer() {
        Path serverDir = Paths.get("./server");
        Path serverPath = serverDir.resolve("server.jar");

        // Check if the server jar exists
        if (Files.exists(serverPath)) {
            long maxMemoryInMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;
            List<String> arguments = List.of("-Xmx" + maxMemoryInMB + "M", "-Xms" + maxMemoryInMB/2 + "M");

            InstanceConfig instanceConfig = instanceConfigRepository.findAll().getFirst();
            String javaPath = "../java/" + instanceConfig.getSelectedJavaVersion() + "/bin/java";

            List<String> command = new ArrayList<>();
            command.add(javaPath);
            command.addAll(arguments);
            command.add("-jar");
            command.add("server.jar");

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(serverDir.toFile());
            processBuilder.inheritIO();

            try {
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                System.out.println("Process exited with code: " + exitCode);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        System.out.println("[!!] Server jar not found, please check your setup");
        return false;
    }


}
