package com.bobost.server_instance.controller;

import com.bobost.server_instance.exception.server.InvalidMCVersionTypeException;
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
    public void downloadJar(@RequestParam String link, @RequestParam String version, @RequestParam String type) {
        //noinspection UnusedAssignment
        MinecraftVersionType minecraftVersionType = MinecraftVersionType.NONE;

        try {
            minecraftVersionType = MinecraftVersionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("[!!] Invalid Minecraft version type: " + type);
            throw new InvalidMCVersionTypeException("Invalid Minecraft version type: " + type);
        }

        lifecycleService.downloadJar(link, version, minecraftVersionType);
    }

    @PostMapping("/uploadJar")
    public void uploadJar(@RequestParam("file") MultipartFile file, @RequestParam String version, @RequestParam String type) {
        //noinspection UnusedAssignment
        MinecraftVersionType minecraftVersionType = MinecraftVersionType.NONE;

        try {
            minecraftVersionType = MinecraftVersionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("[!!] Invalid Minecraft version type: " + type);
            throw new InvalidMCVersionTypeException("Invalid Minecraft version type: " + type);
        }

        lifecycleService.uploadJar(file, version, minecraftVersionType);
    }

    @PostMapping("/run")
    public void runServer() {
        lifecycleService.startServer();
    }

    @PostMapping("/sendCommand")
    public void sendCommand(@RequestParam String command) {
        lifecycleService.sendCommand(command);
    }

    @PostMapping("/stop")
    public void stopServer(@RequestParam(required = false) boolean force) {
        lifecycleService.stopServer(force);
    }
}
