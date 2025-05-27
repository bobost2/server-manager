package com.bobost.panel_back_end.service;

public interface UserService {
    void startupUserCheck();
    boolean hasPasswordExpired(String username);
    boolean hasOTP(String username);
    void updatePassword(String username, String oldPassword, String newPassword);
    void updateUsername(String oldUsername, String newUsername);
}
