package com.bobost.panel_back_end.data.repository;

import com.bobost.panel_back_end.data.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
