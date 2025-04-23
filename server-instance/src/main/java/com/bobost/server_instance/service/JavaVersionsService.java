package com.bobost.server_instance.service;

import java.util.ArrayList;
import java.util.Map;

public interface JavaVersionsService {
    ArrayList<Integer> DownloadableJavaVersions();
    Map<Integer, Boolean> GetInstalledJavaVersions();
    boolean DownloadJavaVersion(int version);
    boolean RemoveJavaVersion(int version);

    boolean SelectJavaVersion(int version);
    int GetSelectedJavaVersion();
}
