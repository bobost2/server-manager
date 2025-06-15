package com.bobost.panel_back_end.service.impl;

import com.bobost.panel_back_end.data.entity.Role;
import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.RoleRepository;
import com.bobost.panel_back_end.data.repository.UserRepository;
import com.bobost.panel_back_end.dto.user.management.ChangePasswordUserDTO;
import com.bobost.panel_back_end.dto.user.management.ChangeUserRoleDTO;
import com.bobost.panel_back_end.dto.user.management.GetUsersDTO;
import com.bobost.panel_back_end.dto.user.management.RegisterUserDTO;
import com.bobost.panel_back_end.exception.role.RoleDoesNotExistException;
import com.bobost.panel_back_end.exception.user.PasswordRequirementNotMetException;
import com.bobost.panel_back_end.exception.user.UserNotFoundException;
import com.bobost.panel_back_end.exception.user.UsernameRequirementsNotMetException;
import com.bobost.panel_back_end.exception.user.management.CannotModifyCurrentProfileException;
import com.bobost.panel_back_end.exception.user.management.RoleRequiredException;
import com.bobost.panel_back_end.service.UserManagementService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(RegisterUserDTO registerUserDTO) {
        User user = new User();

        String username = registerUserDTO.getUsername();
        username = username.trim();

        if (username.length() < 4) {
            throw new UsernameRequirementsNotMetException("New username must be at least 4 characters long");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UsernameRequirementsNotMetException("Username already exists");
        }

        String password = registerUserDTO.getInitPassword();
        if (password.length() < 4) {
            throw new PasswordRequirementNotMetException("New password must be at least 4 characters long");
        }

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordExpired(registerUserDTO.isPasswordExpired());
        user.setAdmin(registerUserDTO.isAdmin());

        if (!registerUserDTO.isAdmin()) {
            if (registerUserDTO.getRoleId() == null) {
                throw new RoleRequiredException("Role is required for non-admin users");
            }

            Role role = roleRepository.findById(registerUserDTO.getRoleId()).orElseThrow(
                    () -> new RoleDoesNotExistException("Role " + registerUserDTO.getRoleId() + " does not exist.")
            );

            user.setRole(role);
        }

        userRepository.save(user);
    }

    @Override
    public List<GetUsersDTO> getUsers(long currentUserId) {
        List<GetUsersDTO> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            GetUsersDTO userDTO = new GetUsersDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setPasswordExpired(user.isPasswordExpired());
            userDTO.setHas2fa(user.isTotpEnabled());

            if (user.getRole() == null) {
                userDTO.setRoleId(null);
                userDTO.setRoleName("No Role");
            } else {
                userDTO.setRoleId(user.getRole().getId());
                userDTO.setRoleName(user.getRole().getName());
            }

            userDTO.setAdmin(user.isAdmin());
            userDTO.setCurrentUser(currentUserId == user.getId());

            users.add(userDTO);
        });
        return users;
    }

    @Override
    public void deleteUser(long adminUserId, long targetUserId) {
        if (adminUserId == targetUserId) {
            throw new CannotModifyCurrentProfileException("Cannot delete your own profile");
        }
        if (!userRepository.existsById(targetUserId)) {
            throw new RoleDoesNotExistException("User " + targetUserId + " does not exist.");
        }
        // TODO: Check if the user has any instances running and handle accordingly
        userRepository.deleteById(targetUserId);
    }

    @Override
    public void resetUserPassword(long adminUserId, ChangePasswordUserDTO changePasswordUserDTO) {
        if (adminUserId == changePasswordUserDTO.getUserId()) {
            throw new CannotModifyCurrentProfileException("Cannot reset your own password from here.");
        }

        long userId = changePasswordUserDTO.getUserId();
        String password = changePasswordUserDTO.getNewPassword();
        if (password.length() < 4) {
            throw new PasswordRequirementNotMetException("New password must be at least 4 characters long");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with ID " + userId + " not found")
        );

        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordExpired(changePasswordUserDTO.isExpirePassword());
        userRepository.save(user);
    }

    @Override
    public void expireUserPassword(long adminUserId, long targetUserId) {
        if (adminUserId == targetUserId) {
            throw new CannotModifyCurrentProfileException("Cannot expire your own password");
        }

        User user = userRepository.findById(targetUserId).orElseThrow(
                () -> new UserNotFoundException("User with ID " + targetUserId + " not found")
        );
        user.setPasswordExpired(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser2FA(long adminUserId, long targetUserId) {
        if (adminUserId == targetUserId) {
            throw new CannotModifyCurrentProfileException("Cannot disable 2FA for your own profile from here.");
        }

        User user = userRepository.findById(targetUserId).orElseThrow(
                () -> new UserNotFoundException("User with ID " + targetUserId + " not found")
        );
        user.setTotpEnabled(false);
        user.setTotpSecret(null);
        userRepository.save(user);
    }

    @Override
    public void changeUserRole(long adminUserId, ChangeUserRoleDTO changeUserRoleDTO) {
        if (adminUserId == changeUserRoleDTO.getUserId()) {
            throw new CannotModifyCurrentProfileException("Cannot change your own role.");
        }

        User user = userRepository.findById(changeUserRoleDTO.getUserId()).orElseThrow(
                () -> new UserNotFoundException("User with ID " + changeUserRoleDTO.getUserId() + " not found")
        );

        if (changeUserRoleDTO.isAdmin()) {
            user.setAdmin(true);
            user.setRole(null);
        } else {
            Long roleId = changeUserRoleDTO.getRoleId();
            if (roleId == null) {
                throw new RoleRequiredException("Role is required for non-admin users");
            }

            Role role = roleRepository.findById(changeUserRoleDTO.getRoleId()).orElseThrow(
                    () -> new RoleDoesNotExistException("Role " + changeUserRoleDTO.getRoleId() + " does not exist.")
            );
            user.setAdmin(false);
            user.setRole(role);
        }

        userRepository.save(user);
    }
}
