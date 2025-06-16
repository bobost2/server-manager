package com.bobost.panel_back_end.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "server_instances")
@Data @NoArgsConstructor @AllArgsConstructor
public class ServerInstance {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, updatable = false)
    private String containerId;

    @Column(nullable = false)
    private String jwtSecret;

    @Column(nullable = false)
    private String status; // Change to enum?
}
