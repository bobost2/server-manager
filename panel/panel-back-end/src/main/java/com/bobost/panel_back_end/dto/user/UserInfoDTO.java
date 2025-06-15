package com.bobost.panel_back_end.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserInfoDTO {
    private String username;
    private List<String> permissions;
}
