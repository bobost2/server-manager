package com.bobost.panel_back_end.filter;

import com.bobost.panel_back_end.exception.user.InvalidOTPCodeException;
import com.bobost.panel_back_end.exception.user.OTPRequiredException;
import com.bobost.panel_back_end.service.TotpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JsonUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TotpService totpService;

    public JsonUsernamePasswordAuthFilter(AuthenticationManager authenticationManager, TotpService totpService) {
        this.totpService = totpService;
        super.setAuthenticationManager(authenticationManager);
        super.setFilterProcessesUrl("/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException
    {
        try {
            final var credentials = mapper.readValue(request.getInputStream(), LoginRequest.class);
            final String username = credentials.username.trim();

            Authentication auth = this.getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            credentials.password
                    )
            );

            if (totpService.isTotpEnabled(username)) {
                if (credentials.code == null || credentials.code.isBlank()) {
                    throw new OTPRequiredException("2FA code is required");
                }
                if (!totpService.verifyLoginCode(username, credentials.code)) {
                    throw new InvalidOTPCodeException("Invalid 2FA code");
                }
            }

            return auth;
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

    }

    public record LoginRequest(String username, String password, String code) { }
}
