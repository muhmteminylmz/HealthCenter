package com.example.health_center.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MedicalReportResponse {

    private Long id;

    private String name;

    private String description;

    private LocalDateTime reportDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private DiseaseResponse diagnosis;

    private PatientResponse patient;

    private DoctorResponse doctor;
}
