package com.bobost.server_instance.data.repository;

import com.bobost.server_instance.data.entity.JavaVersions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JavaVersionsRepository extends JpaRepository<JavaVersions, Integer> {
}
