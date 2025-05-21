package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NurseRepository extends JpaRepository<Nurse,Long> {

    boolean existsByUsername(String username);

    Nurse findByUsernameEquals(String username);

    boolean existsByTc(String tc);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT * FROM nurse ORDER BY RANDOM() LIMIT 2", nativeQuery = true)
    List<Nurse> findTwoRandomNurse();

}
