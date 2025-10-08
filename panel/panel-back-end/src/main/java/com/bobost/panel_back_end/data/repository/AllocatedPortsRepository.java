package com.bobost.panel_back_end.data.repository;

import com.bobost.panel_back_end.data.entity.AllocatedPorts;
import com.bobost.panel_back_end.data.entity.ServerInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AllocatedPortsRepository extends JpaRepository<AllocatedPorts, Long> {
    Optional<AllocatedPorts> findByPort(int port);
    List<AllocatedPorts> findAllByServerInstance(ServerInstance inst);
}
