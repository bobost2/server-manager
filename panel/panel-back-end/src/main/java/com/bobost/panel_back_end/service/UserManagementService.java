package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.dto.user.management.ChangePasswordUserDTO;
import com.bobost.panel_back_end.dto.user.management.ChangeUserRoleDTO;
import com.bobost.panel_back_end.dto.user.management.GetUsersDTO;
import com.bobost.panel_back_end.dto.user.management.RegisterUserDTO;

import java.util.List;

public interface UserManagementService {
    void registerUser(RegisterUserDTO registerUserDTO);
    List<GetUsersDTO> getUsers(long currentUserId);
    void deleteUser(long adminUserId, long targetUserId);

    void resetUserPassword(long adminUserId, ChangePasswordUserDTO changePasswordUserDTO);
    void expireUserPassword(long adminUserId, long targetUserId);
    void disableUser2FA(long adminUserId, long targetUserId);
    void changeUserRole(long adminUserId, ChangeUserRoleDTO changeUserRoleDTO);
}
