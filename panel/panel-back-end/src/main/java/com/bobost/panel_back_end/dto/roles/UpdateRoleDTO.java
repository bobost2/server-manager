package com.bobost.panel_back_end.dto.roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleDTO {
    private long id;
    private String name;
    private Map<String, Boolean> permissions;
}
