package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Disease;
import com.example.health_center.entity.concretes.Patient;
import com.example.health_center.entity.relations.PatientDisease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientDiseaseRepository extends JpaRepository<PatientDisease, Long> {

    boolean existsByPatientAndDisease(Patient patient, Disease disease);

    List<PatientDisease> findAllByPatient(Patient patient); // Ekstra ihtiyaç duyulursa
}

