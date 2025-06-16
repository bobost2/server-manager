package com.bobost.panel_back_end.data.repository;
import com.bobost.panel_back_end.data.entity.ServerInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerInstanceRepository extends JpaRepository<ServerInstance, Long> {
}
