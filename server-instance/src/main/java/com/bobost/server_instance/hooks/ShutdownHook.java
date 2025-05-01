package com.bobost.server_instance.hooks;

import com.bobost.server_instance.service.LifecycleService;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ShutdownHook {
    private final LifecycleService lifecycleService;

    public ShutdownHook(LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("[!!] Shutting down application...");
        if (lifecycleService.isServerRunning()) {
            System.out.println("[!!] Server is running, attempting to stop it...");
            lifecycleService.stopServer(false);
        }
    }
}
