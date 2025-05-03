package com.bobost.server_instance.config;

import com.bobost.server_instance.service.LifecycleService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    private final LifecycleService service;

    public StartupRunner(LifecycleService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        service.onStartup();
    }
}