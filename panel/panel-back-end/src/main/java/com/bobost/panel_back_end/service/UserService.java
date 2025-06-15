package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.dto.user.UserInfoDTO;

public interface UserService {
    void startupUserCheck();
    boolean hasPasswordExpired(long userId);
    void updatePassword(long userId, String oldPassword, String newPassword);
    void updateUsername(long userId, String newUsername);
    UserInfoDTO getUserInfo(long id);
}
