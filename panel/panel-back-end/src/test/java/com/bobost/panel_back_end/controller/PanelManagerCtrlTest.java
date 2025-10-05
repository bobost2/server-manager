package com.bobost.panel_back_end.controller;

import com.bobost.panel_back_end.dto.roles.RoleWithPermsDTO;
import com.bobost.panel_back_end.service.RoleService;
import com.bobost.panel_back_end.service.SecurityService;
import com.bobost.panel_back_end.service.UserManagementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PanelManagement.class)
class PanelManagerCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private UserManagementService userManagementService;

    @MockitoBean(name = "security")
    private SecurityService securityBean;

    @Test
    @WithMockUser(username = "admin")
    void testAdminAccess() throws Exception {
        Mockito.when(securityBean.isAdmin()).thenReturn(true); // This is doing absolutely nothing

        List<RoleWithPermsDTO> roles = List.of(
                new RoleWithPermsDTO(1L, "AllPerms", Map.of("perm1", true, "perm2", true)),
                new RoleWithPermsDTO(2L, "LimitedPerms", Map.of("perm1", false, "perm2", false))
        );

        given(roleService.getAllRoles()).willReturn(roles);

        mockMvc.perform(get("/management/roles/get")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().json(
                        "[{\"id\":1,\"name\":\"AllPerms\",\"permissions\":{\"perm1\":true,\"perm2\":true}}," +
                                "{\"id\":2,\"name\":\"LimitedPerms\",\"permissions\":{\"perm1\":false,\"perm2\":false}}]"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "user")
    void testDefaultUser() throws Exception {
        Mockito.when(securityBean.isAdmin()).thenReturn(false);
        given(roleService.getAllRoles()).willReturn(null);

        mockMvc.perform(get("/management/roles/get")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}
