package com.bobost.panel_back_end.service;

import com.bobost.panel_back_end.data.entity.ServerInstance;

public interface InstanceService {
    ServerInstance createInstance(String name);
}
