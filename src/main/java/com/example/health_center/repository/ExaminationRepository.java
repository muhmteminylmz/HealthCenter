package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Examination;
import com.example.health_center.payload.response.ExaminationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    @Query(value = "SELECT * FROM examination e WHERE CAST(e.examination_date AS DATE) = :date", nativeQuery = true)
    List<Examination> findByExaminationDateEquals(@Param("date") LocalDate date);

    @Query("SELECT e FROM Examination e WHERE e.examinationDate BETWEEN :start AND :end")
    List<Examination> findAllByExaminationDateBetween(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    List<Examination> findAllByDoctorUsername(String username);
    List<Examination> findAllByPatientUsername(String username);

    @Query("SELECT e FROM Examination e JOIN e.nurses n WHERE n.username = :username")
    List<Examination> findAllByNurseUsername(@Param("username") String username);
}
