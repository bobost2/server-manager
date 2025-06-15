package com.bobost.panel_back_end.dto.user.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class RegisterUserDTO {
    private String username;
    private String initPassword;
    private boolean passwordExpired = true;
    private boolean admin = false;
    private Long roleId = null;
}
