package com.bobost.panel_back_end.controller;

import com.bobost.panel_back_end.service.InstanceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instance")
public class InstanceController {

    private final InstanceService instanceService;

    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @PreAuthorize("@security.admin")
    @PostMapping("/create")
    public void createInstance() {
        instanceService.createInstance("sample-instance");
    }
}
