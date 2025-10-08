package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.UserRepository;
import com.bobost.panel_back_end.exception.user.UserNotAuthenticatedException;
import com.bobost.panel_back_end.exception.user.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("security")
public class SecurityService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    public SecurityService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    private User getAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        long userId = Long.parseLong(auth.getName());

        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with ID " + userId + " not found")
        );
    }

    public boolean isAdmin() {
        var user = getAuthUser();
        return user.isAdmin();
    }

    public boolean hasPermission(String permission) {
        var user = getAuthUser();

        if (user.isAdmin()) {
            return true;
        }

        var role = user.getRole();
        if (role == null) {
            return false;
        }

        var roleWithPerms = roleService.getRoleWithPermissions(role);
        if (roleWithPerms.getPermissions().isEmpty() || !roleWithPerms.getPermissions().containsKey(permission)) {
            return false;
        }

        return roleWithPerms.getPermissions().get(permission);
    }

    public boolean isInstanceOwner(Long instanceId) {
        var user = getAuthUser();
        boolean isOwner = false;
        var serverInstances = user.getServerInstances();
        if (serverInstances != null) {
            isOwner = serverInstances.stream().anyMatch(instance -> instance.getId().equals(instanceId));
        }
        return isOwner;
    }
}
