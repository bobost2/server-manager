package com.bobost.panel_back_end.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/info")
    public Map<String,Object> whoami(HttpServletRequest req, Authentication auth) {
        HttpSession sess = req.getSession(false);
        auth.getCredentials();
        return Map.of(
                "cookieHeader", req.getHeader("Cookie"),
                "sessionId",    sess == null ? null : sess.getId(),
                "authenticated", auth != null && auth.isAuthenticated(),
                "principal",     auth != null && auth.isAuthenticated() ? auth.getName() : "",
                "roles",         auth != null && auth.isAuthenticated()
                        ? auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList() : ""
        );
    }
}
