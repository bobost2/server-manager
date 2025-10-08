package com.bobost.panel_back_end.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean admin;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean passwordExpired;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean totpEnabled;

    @Column
    private String totpSecret;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToMany
    @JoinTable(
        name = "server_instance_owners",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "server_instance_id")
    )
    private Set<ServerInstance> serverInstances = new HashSet<>();
}
