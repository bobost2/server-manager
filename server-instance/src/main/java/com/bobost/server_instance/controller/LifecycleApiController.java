package com.bobost.server_instance.controller;

import com.bobost.server_instance.model.MinecraftVersionType;
import com.bobost.server_instance.service.LifecycleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/server")
public class LifecycleApiController {
    LifecycleService lifecycleService;

    public LifecycleApiController(LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @PostMapping("/downloadJar")
    public boolean downloadJar(@RequestParam String link, @RequestParam String version, @RequestParam String type) {
        MinecraftVersionType minecraftVersionType = MinecraftVersionType.NONE;

        try {
            minecraftVersionType = MinecraftVersionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("[!!] Invalid Minecraft version type: " + type);
            return false;
        }

        return lifecycleService.downloadJar(link, version, minecraftVersionType);
    }

    @PostMapping("/uploadJar")
    public boolean uploadJar(@RequestParam("file") MultipartFile file, @RequestParam String version, @RequestParam String type) {
        MinecraftVersionType minecraftVersionType = MinecraftVersionType.NONE;

        try {
            minecraftVersionType = MinecraftVersionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("[!!] Invalid Minecraft version type: " + type);
            return false;
        }

        return lifecycleService.uploadJar(file, version, minecraftVersionType);
    }

    @PostMapping("/run")
    public boolean runServer() {
        return lifecycleService.startServer();
    }

    @PostMapping("/sendCommand")
    public boolean sendCommand(@RequestParam String command) {
        return lifecycleService.sendCommand(command);
    }

    @PostMapping("/stop")
    public boolean stopServer(@RequestParam(required = false) boolean force) {
        return lifecycleService.stopServer(force);
    }
}
