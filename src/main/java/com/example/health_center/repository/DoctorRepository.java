package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByUsername(String username);

    Doctor findByUsernameEquals(String username);

    boolean existsByTc(String tc);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Doctor> findByUsername(String username);

    @Query(value = "SELECT * FROM doctor ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Doctor> findRandomDoctor();

    //tekrar bak TODO
    //@Query("SELECT d FROM Doctor d WHERE d.familyDoctor.username = :username")
    @Query("SELECT d FROM Doctor d WHERE d.username = :username")
    Optional<Doctor> getDoctorByUsername(String username);
}
