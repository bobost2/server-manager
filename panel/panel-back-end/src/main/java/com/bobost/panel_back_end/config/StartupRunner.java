package com.bobost.panel_back_end.config;

import com.bobost.panel_back_end.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    private final UserService userService;

    public StartupRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        userService.startupUserCheck();
    }
}
