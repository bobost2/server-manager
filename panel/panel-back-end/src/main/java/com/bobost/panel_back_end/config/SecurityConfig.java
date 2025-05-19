package com.bobost.panel_back_end.config;

import com.bobost.panel_back_end.filter.JsonUsernamePasswordAuthFilter;
import com.bobost.panel_back_end.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    AuthenticationManager authManager(HttpSecurity http, CustomUserDetailsService customUserDetailsService,
                                      PasswordEncoder passwordEncoder) throws Exception {

        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return authManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("ExtractMethodRecommender")
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        var jsonFilter = new JsonUsernamePasswordAuthFilter(authManager);
        jsonFilter.setAuthenticationSuccessHandler(
                (request, response, authentication) -> {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    HttpSession session = request.getSession();
                    session.setAttribute(
                            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                            SecurityContextHolder.getContext()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                });
        jsonFilter.setAuthenticationFailureHandler(
                (request, response, authentication)
                        -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        );

        http
                .requestCache(RequestCacheConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAt(jsonFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessHandler(
                                (request, response, authentication) ->
                                        response.setStatus(HttpStatus.OK.value())
                        )
                );

        return http.build();
    }
}
