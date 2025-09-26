package com.csi_rscoe.csi_backend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.csi_rscoe.csi_backend.Models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
}
