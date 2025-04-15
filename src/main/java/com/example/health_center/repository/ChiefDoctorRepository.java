package com.example.health_center.repository;

import com.example.health_center.entity.concretes.ChiefDoctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiefDoctorRepository extends JpaRepository<ChiefDoctor, Long> {

    boolean existsByUsername(String username);

    ChiefDoctor findByUsernameEquals(String username);

    boolean existsByTc(String tc);

    boolean existsByPhoneNumber(String phoneNumber);
}
