package com.bobost.panel_back_end.service.impl;

import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.UserRepository;
import com.bobost.panel_back_end.exception.user.IncorrectPasswordException;
import com.bobost.panel_back_end.exception.user.PasswordRequirementNotMetException;
import com.bobost.panel_back_end.exception.user.UserNotFoundException;
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

    @Override
    public boolean hasPasswordExpired(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found - " + username)
        );
        return user.isPasswordExpired();
    }

    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {
        if (newPassword.length() < 4) {
            throw new PasswordRequirementNotMetException("New password must be at least 4 characters long");
        }

        if (newPassword.equals(oldPassword)) {
            throw new PasswordRequirementNotMetException("New password must be different from old password");
        }

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found - " + username)
        );

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IncorrectPasswordException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        if (user.isPasswordExpired()) {
            user.setPasswordExpired(false);
        }

        userRepository.save(user);
    }
}
