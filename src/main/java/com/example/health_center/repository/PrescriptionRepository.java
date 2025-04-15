package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;


public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {


    List<Prescription> findByDoctorUsername(String username);

    List<Prescription> findByPatientUsername(String username);

}
