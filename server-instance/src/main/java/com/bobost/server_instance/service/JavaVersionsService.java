package com.bobost.server_instance.service;

import java.util.ArrayList;
import java.util.Map;

public interface JavaVersionsService {
    void UpdateAdoptiumVersionRepository();
    ArrayList<Integer> DownloadableJavaVersions();
    Map<Integer, Boolean> GetInstalledJavaVersions(boolean refreshRepo);
    default Map<Integer, Boolean> GetInstalledJavaVersions() {
        return GetInstalledJavaVersions(false);
    }
    void DownloadJavaVersion(int version);
    void RemoveJavaVersion(int version);

    void SelectJavaVersion(int version);
    int GetSelectedJavaVersion();
}
