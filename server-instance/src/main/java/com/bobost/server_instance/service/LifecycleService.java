package com.bobost.server_instance.service;

import com.bobost.server_instance.model.MinecraftVersionType;
import org.springframework.web.multipart.MultipartFile;

public interface LifecycleService {
    void onStartup();
    boolean isServerRunning();
    boolean startServer();
    boolean sendCommand(String command);
    boolean stopServer(boolean force);

    boolean downloadJar(String link, String version, MinecraftVersionType type);
    boolean uploadJar(MultipartFile file, String version, MinecraftVersionType type);
}
