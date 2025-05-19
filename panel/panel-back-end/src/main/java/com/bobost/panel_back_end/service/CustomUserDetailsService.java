package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        String role;

        if (user.isAdmin()) {
            role = "ADMIN";
        } else {
            var userRole = user.getRole();
            if (userRole != null) {
                role = userRole.getName(); //TODO: Get the separate permission instead
            } else {
                role = "NONE";
            }
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(role)
                .build();
    }
}
