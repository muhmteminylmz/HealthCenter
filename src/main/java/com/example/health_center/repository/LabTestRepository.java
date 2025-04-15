package com.example.health_center.repository;

import com.example.health_center.entity.concretes.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface LabTestRepository extends JpaRepository<LabTest,Long> {
    List<LabTest> findAllByDoctorUsername(String username);

    List<LabTest> findAllByPatientUsername(String username);
}
