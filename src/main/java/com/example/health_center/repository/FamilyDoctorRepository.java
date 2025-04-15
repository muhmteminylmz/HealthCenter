package com.example.health_center.repository;

import com.example.health_center.entity.concretes.FamilyDoctor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FamilyDoctorRepository extends JpaRepository<FamilyDoctor, Long> {
    void findByDoctorId(Long id);

    @Query("SELECT f FROM FamilyDoctor f LEFT JOIN f.patients p GROUP BY f ORDER BY COUNT(p) ASC")
    List<FamilyDoctor> findTop2FamilyDoctorByPatientCount(Pageable pageable);
}
