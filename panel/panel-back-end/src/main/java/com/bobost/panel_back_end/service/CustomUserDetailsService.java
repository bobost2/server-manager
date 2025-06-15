package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    public CustomUserDetailsService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        List<String> permissions = new ArrayList<>();

        //Deprecated segment, since we are checking the DB instead of the session
        //noinspection DuplicatedCode
        if (user.isAdmin()) {
            permissions.add("ADMIN");
        } else {
            var userRole = user.getRole();
            if (userRole != null) {
                var perms = roleService.getRoleWithPermissions(userRole);
                perms.getPermissions().forEach(
                        (key, value) -> {
                            if (value) {
                                permissions.add(key);
                            }
                        }
                );
            }
        }
        //End of deprecated segment

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getId().toString())
                .password(user.getPassword())
                .authorities(permissions.toArray(String[]::new))
                .build();
    }
}
