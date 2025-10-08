package com.bobost.panel_back_end.data.repository;

import com.bobost.panel_back_end.data.entity.ServerInstance;
import com.bobost.panel_back_end.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByRole_Id(Long roleId);
    List<User> findAllByServerInstancesContains(ServerInstance inst);
}
