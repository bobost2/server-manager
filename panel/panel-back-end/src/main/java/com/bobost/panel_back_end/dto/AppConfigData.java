package com.bobost.panel_back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class AppConfigData {
    private PortSettings portSettings;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PortSettings {
        private List<PortRange> portRanges;
    }
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PortRange {
        private int min;
        private int max;
    }
}
