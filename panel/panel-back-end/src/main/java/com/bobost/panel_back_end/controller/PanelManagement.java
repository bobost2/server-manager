package com.bobost.panel_back_end.controller;

import com.bobost.panel_back_end.dto.roles.CreateRoleDTO;
import com.bobost.panel_back_end.dto.roles.DeleteRoleDTO;
import com.bobost.panel_back_end.dto.roles.RoleWithPermsDTO;
import com.bobost.panel_back_end.dto.roles.UpdateRoleDTO;
import com.bobost.panel_back_end.dto.user.management.*;
import com.bobost.panel_back_end.service.RoleService;
import com.bobost.panel_back_end.service.UserManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/management")
public class PanelManagement {

    private final RoleService roleService;
    private final UserManagementService userManagementService;
    public PanelManagement(RoleService roleService, UserManagementService userManagementService) {
        this.roleService = roleService;
        this.userManagementService = userManagementService;
    }

    //-----------------------------------------------------------
    // Role management
    //-----------------------------------------------------------
    @PostMapping("/roles/create")
    @PreAuthorize("@security.admin")
    public long createRole(@RequestBody CreateRoleDTO createRoleDTO) {
        return roleService.createRole(createRoleDTO);
    }

    @PreAuthorize("@security.admin")
    @PatchMapping("/roles/update")
    public void updateRole(@RequestBody UpdateRoleDTO updateRoleDTO) {
        roleService.updateRole(updateRoleDTO);
    }

    @PreAuthorize("@security.admin")
    @GetMapping("/roles/get")
    public List<RoleWithPermsDTO> getRoles() {
        return roleService.getAllRoles();
    }

    @PreAuthorize("@security.admin")
    @GetMapping("/roles/get-permissions")
    public List<String> getPermissions() {
        return roleService.getAllPermissions();
    }

    @PreAuthorize("@security.admin")
    @DeleteMapping("/roles/delete")
    public void deleteRole(@RequestBody DeleteRoleDTO deleteRoleDTO) {
        roleService.deleteRole(deleteRoleDTO.getRoleId());
    }

    //-----------------------------------------------------------
    // User management
    //-----------------------------------------------------------
    @PreAuthorize("@security.admin")
    @PostMapping("/register-user")
    public long registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        return userManagementService.registerUser(registerUserDTO);
    }

    @PreAuthorize("@security.admin")
    @PatchMapping("/reset-user-password")
    public void resetUserPassword(Principal principal, @RequestBody ChangePasswordUserDTO changePasswordUserDTO) {
        userManagementService.resetUserPassword(Long.parseLong(principal.getName()), changePasswordUserDTO);
    }

    @PreAuthorize("@security.admin")
    @PatchMapping("/expire-user-password")
    public void expireUserPassword(Principal principal, @RequestBody ExpirePasswordUserDTO expirePasswordUserDTO) {
        userManagementService.expireUserPassword(Long.parseLong(principal.getName()), expirePasswordUserDTO.getUserId());
    }

    @PreAuthorize("@security.admin")
    @PatchMapping("/reset-user-2fa")
    public void resetUser2FA(Principal principal, @RequestBody Reset2FAUserDTO reset2FAUserDTO) {
        userManagementService.disableUser2FA(Long.parseLong(principal.getName()), reset2FAUserDTO.getUserId());
    }

    @PreAuthorize("@security.admin")
    @PatchMapping("/change-user-role")
    public void changeUserRole(Principal principal, @RequestBody ChangeUserRoleDTO changeUserRoleDTO) {
        userManagementService.changeUserRole(Long.parseLong(principal.getName()), changeUserRoleDTO);
    }

    @PreAuthorize("@security.admin")
    @GetMapping("/get-users")
    public List<GetUsersDTO> getUsers(Principal principal) {
        return userManagementService.getUsers(Long.parseLong(principal.getName()));
    }

    @PreAuthorize("@security.admin")
    @DeleteMapping("/delete-user")
    public void deleteUser(Principal principal, @RequestBody DeleteUserDTO deleteUserDTO) {
        userManagementService.deleteUser(Long.parseLong(principal.getName()), deleteUserDTO.getUserId());
    }
}
