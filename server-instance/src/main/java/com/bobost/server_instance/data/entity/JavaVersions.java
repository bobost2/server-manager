package com.bobost.server_instance.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class JavaVersions {
    @Id
    private Integer version;

    public JavaVersions() {
    }

    public JavaVersions(Integer version) {
        this.version = version;
    }
}
