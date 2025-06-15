package com.bobost.panel_back_end.service.impl;

import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.UserRepository;
import com.bobost.panel_back_end.dto.user.UserInfoDTO;
import com.bobost.panel_back_end.exception.user.IncorrectPasswordException;
import com.bobost.panel_back_end.exception.user.PasswordRequirementNotMetException;
import com.bobost.panel_back_end.exception.user.UserNotFoundException;
import com.bobost.panel_back_end.exception.user.UsernameRequirementsNotMetException;
import com.bobost.panel_back_end.service.RoleService;
import com.bobost.panel_back_end.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
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
    public boolean hasPasswordExpired(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with ID " + userId + " not found")
        );
        return user.isPasswordExpired();
    }

    @Override
    public void updatePassword(long userId, String oldPassword, String newPassword) {
        if (newPassword.length() < 4) {
            throw new PasswordRequirementNotMetException("New password must be at least 4 characters long");
        }

        if (newPassword.equals(oldPassword)) {
            throw new PasswordRequirementNotMetException("New password must be different from old password");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with ID " + userId + " not found")
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

    @Override
    public void updateUsername(long userId, String newUsername) {
        newUsername = newUsername.trim();

        if (newUsername.length() < 4) {
            throw new UsernameRequirementsNotMetException("New username must be at least 4 characters long");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with ID " + userId + " not found")
        );

        String oldUsername = user.getUsername();
        if (oldUsername.equals(newUsername)) {
            throw new UsernameRequirementsNotMetException("New username must be different from old username");
        }

        if (userRepository.existsByUsername(newUsername)) {
            throw new UsernameRequirementsNotMetException("Username is already taken");
        }

        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Override
    public UserInfoDTO getUserInfo(long id) {
        UserInfoDTO userInfo = new UserInfoDTO();
        List<String> permissions = new ArrayList<>();

        User user = userRepository.findById(id).orElseThrow(
            () -> new UserNotFoundException("User not found with ID: " + id)
        );

        userInfo.setUsername(user.getUsername());

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

        userInfo.setPermissions(permissions);
        return userInfo;
    }
}
