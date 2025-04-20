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

        // TODO: Service implementation
        System.out.println(minecraftVersionType);
        return false;
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

        // TODO: Service implementation
        System.out.println(minecraftVersionType);
        return false;
    }
}
