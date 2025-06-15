package com.bobost.panel_back_end.dto.user.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class GetUsersDTO {
    private Long id;
    private String username;
    private boolean passwordExpired;
    private boolean has2fa;

    private Long roleId;
    private String roleName;
    private boolean isAdmin;

    private boolean currentUser;
}
