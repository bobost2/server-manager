package com.bobost.panel_back_end.dto.instance.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class InstanceStatsDTO {
    private Long id;
    private String friendlyName;
    private boolean isRunning;
}
