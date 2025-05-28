package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {


    boolean existsByCode(String code);

    @Query("select a.code from Allergy a")
    List<String> findAllCodes();

    @Query("select a from Allergy a join a.patients p where p.id = ?1")
    List<Allergy> findAllByPatientId(Long patientId);
}
