package com.bobost.server_instance.data.entity;

import com.bobost.server_instance.model.MinecraftVersionType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class InstanceConfig {
    public InstanceConfig() {
    }

    public InstanceConfig(Integer selectedJavaVersion, MinecraftVersionType selectedMinecraftVersionType, String selectedMinecraftVersion) {
        this.selectedJavaVersion = selectedJavaVersion;
        this.selectedMinecraftVersionType = selectedMinecraftVersionType;
        this.selectedMinecraftVersion = selectedMinecraftVersion;
    }

    @Id
    @GeneratedValue
    private Long id;
    private Integer selectedJavaVersion;

    @Enumerated(EnumType.STRING)
    private MinecraftVersionType selectedMinecraftVersionType;

    private String selectedMinecraftVersion;
}
