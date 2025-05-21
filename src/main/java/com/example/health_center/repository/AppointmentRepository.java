package com.example.health_center.repository;

import com.example.health_center.entity.concretes.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    boolean existsByPatientIdAndAppointmentDateBetween(Long patientId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDoctorUsername(String username);

    List<Appointment> findByPatientUsername(String username);


}
