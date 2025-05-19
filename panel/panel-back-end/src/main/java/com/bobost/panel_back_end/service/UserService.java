package com.bobost.panel_back_end.service;

public interface UserService {
    void startupUserCheck();
    boolean hasPasswordExpired(String username);
    void updatePassword(String username, String oldPassword, String newPassword);
}
