package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.data.entity.ServerInstance;
import com.bobost.panel_back_end.dto.instance.management.CreateInstanceDTO;
import com.bobost.panel_back_end.dto.instance.management.InstanceStatsDTO;

import java.util.List;

public interface InstanceService {
    long createInstance(long adminUserId, CreateInstanceDTO createInstanceDTO);
    List<ServerInstance> listInstances();
    void startInstance(Long id);
    void stopInstance(Long id);
    void deleteInstance(Long id);

    InstanceStatsDTO getInstanceStats(Long id);
    List<InstanceStatsDTO> getAllInstances(Long userId);

    String returnInstanceJavaVersion(Long id);

    // Utility methods
    boolean isPortValid(int port);
    int getNextAvailablePort();

    String generateNameFromFriendlyName(String friendlyName);
    boolean areUserIdsValid(List<Long> userIds);
}
