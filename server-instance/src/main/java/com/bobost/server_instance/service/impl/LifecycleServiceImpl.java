package com.bobost.server_instance.service.impl;

import com.bobost.server_instance.data.entity.InstanceConfig;
import com.bobost.server_instance.data.repository.InstanceConfigRepository;
import com.bobost.server_instance.exception.server.*;
import com.bobost.server_instance.model.MinecraftVersionType;
import com.bobost.server_instance.service.LifecycleService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LifecycleServiceImpl implements LifecycleService {

    InstanceConfigRepository instanceConfigRepository;
    private volatile ServerProcess serverProcess;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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

        if (instanceConfig.getSelectedMinecraftVersionType() != MinecraftVersionType.NONE) {
            System.out.println("[!] Trying to load server jar...");
            try {
                startServer();
            } catch (JarMissingException | ServerRunningException e) {
                System.out.println("[!!] " + e.getMessage());
            }
        } else {
            System.out.println("[!!] No version selected, manual setup required");
        }
    }

    @Override
    public synchronized boolean isServerRunning() {
        return serverProcess != null && serverProcess.isAlive();
    }

    @Override
    public synchronized void startServer() throws ServerRunningException, JarMissingException {
        // Don’t start if already running
        if (serverProcess != null && serverProcess.isAlive()) {
            throw new ServerRunningException("Server is already running.");
        }

        Path serverDir = Paths.get("./data/server");
        Path serverPath = serverDir.resolve("server.jar");

        // Check if the server jar exists
        if (Files.exists(serverPath)) {
            InstanceConfig instanceConfig = instanceConfigRepository.findAll().getFirst();

            executor.submit(() -> {
                try {
                    ProcessBuilder pb = createProcessBuilder(instanceConfig.getSelectedJavaVersion(), serverDir.toFile());
                    Process process = pb.start();
                    serverProcess = new ServerProcess(process);
                    serverProcess.readConsole(); // this will block until console closes
                } catch (IOException e) {
                    throw new ServerRuntimeException(e.toString());
                }
            });
        } else {
            throw new JarMissingException("Server jar not found, please check your setup");
        }
    }

    @Override
    public synchronized void sendCommand(String command) {
        if (serverProcess != null && serverProcess.isAlive()) {
            serverProcess.send(command);
        } else {
            throw new ServerNotRunningException("Server is not running.");
        }
    }

    @Override
    public synchronized void stopServer(boolean force) throws ServerNotRunningException {
        if (serverProcess != null && serverProcess.isAlive()) {
            if (force) {
                serverProcess.stop();
            } else {
                try {
                    serverProcess.send("stop");
                    serverProcess.process.waitFor();
                } catch (InterruptedException e) {
                    throw new ServerRuntimeException(e.toString());
                }
            }
        } else {
            throw new ServerNotRunningException("Server is not running.");
        }
    }

    @Override
    public void downloadJar(String link, String version, MinecraftVersionType type) {
        URI uri = URI.create(link);
        Path serverDir = Paths.get("./data/server", "/server.jar");

        try (InputStream inputStream = uri.toURL().openStream()) {
            Files.copy(inputStream, serverDir, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[!] Downloaded server jar from " + link);

            System.out.println("[!] Updating server config...");
            InstanceConfig instanceConfig = instanceConfigRepository.findAll().getFirst();
            instanceConfig.setSelectedMinecraftVersionType(type);
            instanceConfig.setSelectedMinecraftVersion(version);
            instanceConfigRepository.save(instanceConfig);
            System.out.println("[!] Updated server config");
        } catch (IOException e) {
            throw new JarDownloadFailureException(e.toString());
        }
    }

    @Override
    public void uploadJar(MultipartFile file, String version, MinecraftVersionType type) {
        Path serverDir = Paths.get("./data/server", "/server.jar");
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, serverDir, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[!] Uploaded server jar from " + file.getOriginalFilename());

            System.out.println("[!] Updating server config...");
            InstanceConfig instanceConfig = instanceConfigRepository.findAll().getFirst();
            instanceConfig.setSelectedMinecraftVersionType(type);
            instanceConfig.setSelectedMinecraftVersion(version);
            instanceConfigRepository.save(instanceConfig);
            System.out.println("[!] Updated server config");
        } catch (IOException e) {
            throw new JarUploadFailureException(e.toString());
        }
    }


    // Server related methods

    private ProcessBuilder createProcessBuilder(int javaVersion, File serverDir) {
        long maxMemoryInMB = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        List<String> cmd = List.of(
                "../java/" + javaVersion + "/bin/java",
                "-Xmx" + maxMemoryInMB + "M",
                "-Xms" + (maxMemoryInMB/2) + "M",
                "-jar", "server.jar"
        );
        return new ProcessBuilder(cmd)
                .directory(serverDir)
                //.inheritIO()
                .redirectErrorStream(true);
    }

    private static class ServerProcess {
        private final Process process;
        private final BufferedWriter stdin;
        private final BufferedReader stdout;

        ServerProcess(Process process) {
            this.process = process;
            this.stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            this.stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        }

        boolean isAlive() {
            return process.isAlive();
        }

        void readConsole() {
            try {
                String line;
                while ((line = stdout.readLine()) != null) {
                    // Push to WebSocket or log it
                    System.out.println("[MC] " + line);
                }
            } catch (IOException e) {
                System.out.println("[!!] Error reading console output: " + e.getMessage());
            }
        }

        synchronized void send(String command) {
            try {
                stdin.write(command);
                stdin.newLine();
                stdin.flush();
            } catch (IOException e) {
                throw new SendServerCommandException(e.toString());
            }
        }

        void stop() {
            process.destroy();
        }
    }
}
