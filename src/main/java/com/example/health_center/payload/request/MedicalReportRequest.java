package com.example.health_center.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MedicalReportRequest {

    @NotNull(message = "Name cannot be null")
    private String name;

    private String description;

    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    @Future(message = "End date cannot be in the past")
    private LocalDate endDate;

    @NotNull(message = "Diagnosis id cannot be null")
    private Long diagnosisId;

    @NotNull(message = "Patient id cannot be null")
    private Long patientId;

    @NotNull(message = "Doctor id cannot be null")
    private Long doctorId;
}
