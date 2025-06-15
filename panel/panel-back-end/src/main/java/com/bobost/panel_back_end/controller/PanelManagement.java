package com.bobost.panel_back_end.controller;

import com.bobost.panel_back_end.dto.roles.CreateRoleDTO;
import com.bobost.panel_back_end.dto.roles.DeleteRoleDTO;
import com.bobost.panel_back_end.dto.roles.RoleWithPermsDTO;
import com.bobost.panel_back_end.dto.roles.UpdateRoleDTO;
import com.bobost.panel_back_end.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/management")
public class PanelManagement {

    private final RoleService roleService;
    public PanelManagement(RoleService roleService) {
        this.roleService = roleService;
    }

    //-----------------------------------------------------------
    // Role management
    //-----------------------------------------------------------
    @PostMapping("/roles/create")
    @PreAuthorize("@security.admin")
    public void createRole(@RequestBody CreateRoleDTO createRoleDTO) {
        roleService.createRole(createRoleDTO);
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
    @DeleteMapping("/roles/delete")
    public void deleteRole(@RequestBody DeleteRoleDTO deleteRoleDTO) {
        roleService.deleteRole(deleteRoleDTO.getRoleId());
    }

    //-----------------------------------------------------------
    // User management
    //-----------------------------------------------------------
    @PreAuthorize("@security.admin")
    @PostMapping("/register-user")
    public void registerUser() {

    }

    @PreAuthorize("@security.admin")
    @PatchMapping("/update-user")
    public void updateUser() {

    }

    @PreAuthorize("@security.admin")
    @GetMapping("/get-users")
    public void getUsers() {

    }

    @PreAuthorize("@security.admin")
    @DeleteMapping("/delete-user")
    public void deleteUser() {

    }
}
