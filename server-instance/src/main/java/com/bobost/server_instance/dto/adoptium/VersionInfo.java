package com.bobost.server_instance.dto.adoptium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionInfo {
    private int build;
    private int major;
    private int minor;
    private int security;
    private String openjdk_version;
}
