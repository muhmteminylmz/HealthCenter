package com.example.health_center.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PatientDiseaseRequest {

    @NotNull(message = "patientId cannot be null")
    private Long patientId;

    @NotNull(message = "diseaseId cannot be null")
    private Long diseaseId;

    @PastOrPresent
    private LocalDate diagnosisDate;
}
