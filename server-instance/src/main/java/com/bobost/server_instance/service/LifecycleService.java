package com.bobost.server_instance.service;

public interface LifecycleService {
    void onStartup();
    boolean runServer();
}
