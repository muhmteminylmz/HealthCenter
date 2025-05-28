package com.example.health_center.repository;

import com.example.health_center.entity.concretes.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {


    List<MedicalReport> findAllByPatientId(Long id);
    List<MedicalReport> findAllByDoctorId(Long id);
}
