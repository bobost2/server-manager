package com.bobost.panel_back_end.filter;

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

    public JsonUsernamePasswordAuthFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        super.setFilterProcessesUrl("/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException
    {
        try {
            var credentials = mapper.readValue(request.getInputStream(), LoginRequest.class);
            var token = new UsernamePasswordAuthenticationToken(
                    credentials.username,
                    credentials.password
            );
            return this.getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

    }

    public record LoginRequest(String username, String password) { }
}
