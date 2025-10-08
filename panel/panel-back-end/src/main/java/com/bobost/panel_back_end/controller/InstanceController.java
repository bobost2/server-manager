package com.bobost.panel_back_end.controller;

import com.bobost.panel_back_end.dto.instance.management.CreateInstanceDTO;
import com.bobost.panel_back_end.dto.instance.management.InstanceOperationDTO;
import com.bobost.panel_back_end.dto.instance.management.InstanceStatsDTO;
import com.bobost.panel_back_end.service.InstanceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/instance")
public class InstanceController {

    private final InstanceService instanceService;

    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @PreAuthorize("@security.admin")
    @PostMapping("/create")
    public long createInstance(Principal principal, @RequestBody CreateInstanceDTO createInstanceDTO) {
        return instanceService.createInstance(Long.parseLong(principal.getName()), createInstanceDTO);
    }

    @PreAuthorize("@security.admin")
    @DeleteMapping("/delete")
    public void deleteInstance(@RequestBody InstanceOperationDTO instanceOperationDTO) {
        instanceService.deleteInstance(instanceOperationDTO.getInstanceId());
    }

    @PreAuthorize("@security.isInstanceOwner(#instanceOperationDTO.instanceId) or @security.admin")
    @PostMapping("/start")
    public void startInstance(@RequestBody InstanceOperationDTO instanceOperationDTO) {
        instanceService.startInstance(instanceOperationDTO.getInstanceId());
    }

    @PreAuthorize("@security.isInstanceOwner(#instanceOperationDTO.instanceId) or @security.admin")
    @PostMapping("/stop")
    public void stopInstance(@RequestBody InstanceOperationDTO instanceOperationDTO) {
        instanceService.stopInstance(instanceOperationDTO.getInstanceId());
    }

    @PreAuthorize("@security.isInstanceOwner(#instanceOperationDTO.instanceId) or @security.admin")
    @GetMapping("/info")
    public InstanceStatsDTO getInstanceInfo(@RequestBody InstanceOperationDTO instanceOperationDTO) {
        return instanceService.getInstanceStats(instanceOperationDTO.getInstanceId());
    }

    @GetMapping("/list")
    public java.util.List<InstanceStatsDTO> listInstances(Principal principal) {
        return instanceService.getAllInstances(Long.parseLong(principal.getName()));
    }

    @PreAuthorize("@security.admin")
    @GetMapping("/get-java-version")
    public String getJavaVersion() {
        return instanceService.returnInstanceJavaVersion(102L);
    }

}
