package com.bobost.panel_back_end.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "allocated_ports")
@Data @NoArgsConstructor @AllArgsConstructor
public class AllocatedPorts {
    @Id
    @Column(nullable = false, unique = true)
    private int port;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instance_id")
    private ServerInstance serverInstance;
}
