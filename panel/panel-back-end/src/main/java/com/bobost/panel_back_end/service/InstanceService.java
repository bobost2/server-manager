package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.data.entity.ServerInstance;

import java.util.List;

public interface InstanceService {
    ServerInstance createInstance(String name);
    List<ServerInstance> listInstances();
    void stopInstance(Long id);

    String returnInstanceJavaVersion(Long id);
}
