package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    boolean existsByUsername(String username);

    boolean existsByTc(String tc);

    boolean existsByPhoneNumber(String phoneNumber);

    Admin findByUsernameEquals(String username);
    //deneme
}
