package com.example.health_center.payload.request;

import com.example.health_center.entity.concretes.Allergy;
import com.example.health_center.entity.concretes.Disease;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ExaminationRequest {

    @NotNull(message = "Examination date is required")
    @Future(message = "Examination date must be in the future")
    private LocalDateTime examinationDate;


    private List<Long> allergy_Ids;

    @NotNull(message = "Diagnosis is required")
    private List<Long> disease_Ids;

    @NotNull(message = "Diagnosis is required")
    private String diagnosis;

    @NotNull(message = "Appointment id is required")
    private Long appointmentId;
}
