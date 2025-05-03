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
    public Map<Integer, Boolean> getJavaVersions(@RequestParam(defaultValue = "false") boolean refreshRepo) {
        return javaVersionsService.GetInstalledJavaVersions(refreshRepo);
    }

    @PostMapping("/download/{version}")
    public void downloadJavaVersion(@PathVariable int version) {
        javaVersionsService.DownloadJavaVersion(version);
    }

    @DeleteMapping("/remove/{version}")
    public void removeJavaVersion(@PathVariable int version) {
        javaVersionsService.RemoveJavaVersion(version);
    }

    @PostMapping("/select/{version}")
    public void selectJavaVersion(@PathVariable int version) {
        javaVersionsService.SelectJavaVersion(version);
    }

    @GetMapping("/selected")
    public int getSelectedJavaVersion() {
        return javaVersionsService.GetSelectedJavaVersion();
    }
}
