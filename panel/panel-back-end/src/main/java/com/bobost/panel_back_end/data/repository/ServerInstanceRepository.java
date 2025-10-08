package com.bobost.panel_back_end.data.repository;
import com.bobost.panel_back_end.data.entity.ServerInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServerInstanceRepository extends JpaRepository<ServerInstance, Long> {
    Optional<ServerInstance> findByName(String instanceName);
    List<ServerInstance> findAllByNameStartingWith(String instanceName);
    Optional<ServerInstance> findByPort(int port);
}
