package com.example.health_center.payload.response;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PatientDiseaseResponse {

    private Long id;
    private DiseaseResponse disease;
    private LocalDate diagnosisDate;
}
