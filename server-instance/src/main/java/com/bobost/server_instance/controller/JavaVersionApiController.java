package com.bobost.server_instance.controller;

import com.bobost.server_instance.service.JavaVersionsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/java-versions")
public class JavaVersionApiController {
    private final JavaVersionsService javaVersionsService;

    public JavaVersionApiController(JavaVersionsService javaVersionsService) {
        this.javaVersionsService = javaVersionsService;
    }

    @GetMapping
    public Map<Integer, Boolean> getJavaVersions() {
        return javaVersionsService.GetInstalledJavaVersions();
    }

    @PostMapping("/download")
    public boolean downloadJavaVersion(int version) {
        return javaVersionsService.DownloadJavaVersion(version);
    }

    @DeleteMapping("/remove")
    public boolean removeJavaVersion(int version) {
        return javaVersionsService.RemoveJavaVersion(version);
    }
}
