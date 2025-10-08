package com.bobost.panel_back_end.data.repository;

import com.bobost.panel_back_end.data.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {
}
