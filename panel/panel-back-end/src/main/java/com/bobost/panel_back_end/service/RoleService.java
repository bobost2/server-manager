package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.data.entity.Role;
import com.bobost.panel_back_end.dto.roles.CreateRoleDTO;
import com.bobost.panel_back_end.dto.roles.RoleWithPermsDTO;
import com.bobost.panel_back_end.dto.roles.UpdateRoleDTO;

import java.util.List;
import java.util.Map;

public interface RoleService {
    List<RoleWithPermsDTO> getAllRoles();
    RoleWithPermsDTO getRoleWithPermissions(Role role);
    Role convertPermListToRole(Map<String, Boolean> permissions, Role role);
    void createRole(CreateRoleDTO roleRequest);
    void updateRole(UpdateRoleDTO roleRequest);
    void deleteRole(Long roleId);
}
