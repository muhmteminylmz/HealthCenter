package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient,Long> {

    boolean existsByUsername(String username);

    Patient findByUsernameEquals(String username);

    boolean existsByTc(String tc);

    boolean existsByPhoneNumber(String phoneNumber);

    //@Query("SELECT p FROM Patient p WHERE p.familyDoctor.doctor.username = :username")
    @Query("SELECT p FROM Patient p JOIN p.familyDoctor fd JOIN fd.doctor d WHERE d.username = :username")
    List<Patient> findAllByFamilyDoctorUsername(@Param("username") String username);
}
