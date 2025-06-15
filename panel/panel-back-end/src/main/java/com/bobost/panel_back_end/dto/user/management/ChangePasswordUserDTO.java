package com.bobost.panel_back_end.dto.user.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChangePasswordUserDTO {
    private long userId;
    private String newPassword;
    private boolean expirePassword = true;
}
