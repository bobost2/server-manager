package com.bobost.panel_back_end.dto.instance.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateInstanceDTO {
    private String name;
    private Integer port;
    private List<Long> userOwnerIds;
    private InstanceLimitsDTO limits;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class InstanceLimitsDTO {
        private boolean limitMemory = false;
        private Integer memoryLimitMB;

        private boolean limitCPU = false;
        private Float amountOfCPUs;

        // Not supported by Docker, some twisted workaround would be needed
        //private boolean limitStorage;
        //private Integer storageLimitMB;
    }
}
