package com.bobost.server_instance.service;

import com.bobost.server_instance.exception.server.ServerNotRunningException;
import com.bobost.server_instance.model.MinecraftVersionType;
import org.springframework.web.multipart.MultipartFile;

public interface LifecycleService {
    void onStartup();
    boolean isServerRunning();
    void startServer();
    void sendCommand(String command);
    void stopServer(boolean force) throws ServerNotRunningException;

    void downloadJar(String link, String version, MinecraftVersionType type);
    void uploadJar(MultipartFile file, String version, MinecraftVersionType type);
}
