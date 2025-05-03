package com.bobost.server_instance.service;

import java.util.ArrayList;
import java.util.Map;

public interface JavaVersionsService {
    ArrayList<Integer> DownloadableJavaVersions();
    Map<Integer, Boolean> GetInstalledJavaVersions();
    void DownloadJavaVersion(int version);
    void RemoveJavaVersion(int version);

    void SelectJavaVersion(int version);
    int GetSelectedJavaVersion();
}
