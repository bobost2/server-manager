package com.bobost.panel_back_end.service.impl;

import com.bobost.panel_back_end.data.entity.Role;
import com.bobost.panel_back_end.data.repository.RoleRepository;
import com.bobost.panel_back_end.data.repository.UserRepository;
import com.bobost.panel_back_end.dto.roles.CreateRoleDTO;
import com.bobost.panel_back_end.dto.roles.RoleWithPermsDTO;
import com.bobost.panel_back_end.dto.roles.UpdateRoleDTO;
import com.bobost.panel_back_end.exception.role.*;
import com.bobost.panel_back_end.service.RoleService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<RoleWithPermsDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleWithPermsDTO> roleWithPermsDTO = new ArrayList<>();

        for (Role role : roles) {
            roleWithPermsDTO.add(getRoleWithPermissions(role));
        }
        return roleWithPermsDTO;
    }

    @Override
    public RoleWithPermsDTO getRoleWithPermissions(Role role) {
        RoleWithPermsDTO roleWithPermsDTO = new RoleWithPermsDTO();

        if (role != null) {
            roleWithPermsDTO.setId(role.getId());
            roleWithPermsDTO.setName(role.getName());

            Map<String,Boolean> perms = new LinkedHashMap<>();
            for (Field field : role.getClass().getDeclaredFields()) {
                if (field.getType() == boolean.class || field.getType() == Boolean.class
                        || field.getName().startsWith("perm_")) {
                    field.setAccessible(true);
                    try {
                        perms.put(field.getName(), (Boolean) field.get(role));
                    } catch (IllegalAccessException e) {
                        throw new RoleReflectAccessException(e.getMessage());
                    }
                }
            }
            roleWithPermsDTO.setPermissions(perms);
        }

        return roleWithPermsDTO;
    }

    @Override
    public Role convertPermListToRole(Map<String, Boolean> permissions, Role role) {

        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            String permName = entry.getKey();
            Boolean permValue = entry.getValue();

            try {
                Field field = role.getClass().getDeclaredField(permName);
                if (field.getType() == boolean.class || field.getType() == Boolean.class
                        || field.getName().startsWith("perm_")) {
                    field.setAccessible(true);
                    field.set(role, permValue);
                }
            }
            catch (NoSuchFieldException e) {
                throw new NoSuchPermissionExistsException("Permission " + permName + " does not exist.");
            }
            catch (IllegalAccessException e) {
                throw new RoleReflectAccessException(e.getMessage());
            }
        }

        return role;
    }

    @Override
    public void createRole(CreateRoleDTO roleRequest) {
        Role role = new Role();

        if (roleRepository.existsByName(roleRequest.getName())) {
            throw new RoleNameAlreadyExistsException("Role name " + roleRequest.getName() + " already exists.");
        }

        role.setName(roleRequest.getName());

        Map<String, Boolean> permissions = roleRequest.getPermissions();
        role = convertPermListToRole(permissions, role);

        roleRepository.save(role);
    }

    @Override
    public void updateRole(UpdateRoleDTO roleRequest) {
        Role role = roleRepository.findById(roleRequest.getId())
                .orElseThrow(() -> new RoleDoesNotExistException("Role " + roleRequest.getId() + " does not exist."));

        if (!role.getName().equals(roleRequest.getName()) && roleRepository.existsByName(roleRequest.getName())) {
            throw new RoleNameAlreadyExistsException("Role name " + roleRequest.getName() + " already exists.");
        }
        role.setName(roleRequest.getName());

        Map<String, Boolean> permissions = roleRequest.getPermissions();
        role = convertPermListToRole(permissions, role);

        roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RoleDoesNotExistException("Role " + roleId + " does not exist.");
        }

        // Find if user has this role
        if (userRepository.existsByRole_Id(roleId)) {
            throw new RoleHasBeenAssignedException("Role " + roleId + " has been assigned to a user and cannot be deleted.");
        }

        // Delete the role
        roleRepository.deleteById(roleId);
    }
}
