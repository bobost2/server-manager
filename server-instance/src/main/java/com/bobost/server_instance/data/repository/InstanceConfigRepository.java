package com.bobost.server_instance.data.repository;

import com.bobost.server_instance.data.entity.InstanceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceConfigRepository extends JpaRepository<InstanceConfig, Long> {
}
