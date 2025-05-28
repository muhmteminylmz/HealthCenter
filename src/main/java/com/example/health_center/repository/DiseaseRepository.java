package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Disease;
import com.example.health_center.payload.response.AppointmentResponse;
import com.example.health_center.payload.response.DiseaseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {


    boolean existsByCode(String code);

    @Query("SELECT code FROM Disease")
    List<String> findAllCodes();


/*    @Query("SELECT d FROM Disease d WHERE d.patient.id = ?1")
    List<Disease> findAllByPatientId(Long patientId);*/
}
