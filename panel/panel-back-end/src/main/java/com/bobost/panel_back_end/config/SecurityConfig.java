package com.bobost.panel_back_end.config;

import com.bobost.panel_back_end.exception.user.InvalidOTPCodeException;
import com.bobost.panel_back_end.exception.user.OTPRequiredException;
import com.bobost.panel_back_end.filter.JsonUsernamePasswordAuthFilter;
import com.bobost.panel_back_end.service.CustomUserDetailsService;
import com.bobost.panel_back_end.service.TotpService;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final TotpService totpService;

    public SecurityConfig(TotpService totpService) {
        this.totpService = totpService;
    }

    @Bean
    AuthenticationManager authManager(HttpSecurity http, CustomUserDetailsService customUserDetailsService,
                                      PasswordEncoder passwordEncoder) throws Exception {

        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return authManagerBuilder.build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        var jsonFilter = new JsonUsernamePasswordAuthFilter(authManager, totpService);
        jsonFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
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
                (request, response, exception) -> {
                    response.setContentType("application/json");
                    if (exception instanceof InvalidOTPCodeException) {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().print("{\"error\": \"OTP_INVALID\"}");
                    } else if (exception instanceof OTPRequiredException) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().print("{\"error\": \"OTP_REQUIRED\"}");
                    } else {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().print("{\"error\": \"INVALID_CREDENTIALS\"}");
                    }
                }
        );

        http
                .requestCache(RequestCacheConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(-1)
                        .sessionRegistry(sessionRegistry())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAt(jsonFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(
                                (request, response, authentication) ->
                                        response.setStatus(HttpStatus.OK.value())
                        )
                );

        return http.build();
    }
}
