package com.bobost.panel_back_end.service.impl;

import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.UserRepository;
import com.bobost.panel_back_end.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void startupUserCheck() {
        if (userRepository.count() == 0) {
            User defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword(passwordEncoder.encode("admin"));
            defaultUser.setAdmin(true);
            defaultUser.setPasswordExpired(true);

            userRepository.save(defaultUser);
        }
    }
}
