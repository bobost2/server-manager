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

    @PostMapping("/download/{version}")
    public boolean downloadJavaVersion(@PathVariable int version) {
        return javaVersionsService.DownloadJavaVersion(version);
    }

    @DeleteMapping("/remove/{version}")
    public boolean removeJavaVersion(@PathVariable int version) {
        return javaVersionsService.RemoveJavaVersion(version);
    }

    @PostMapping("/select/{version}")
    public boolean selectJavaVersion(@PathVariable int version) {
        return javaVersionsService.SelectJavaVersion(version);
    }

    @GetMapping("/selected")
    public int getSelectedJavaVersion() {
        return javaVersionsService.GetSelectedJavaVersion();
    }
}
