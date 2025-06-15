package com.bobost.panel_back_end.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession
public class SessionConfig {
    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher>
    httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(
                new HttpSessionEventPublisher()
        );
    }
}

