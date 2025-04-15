package com.example.health_center.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PrescriptionRequest {

    @NotNull(message = "Please enter medicine name")
    @Size(min = 2,max = 16,message = "Your medicine name should be at least between 2-16 chars")
    private String medicine;

    @NotNull(message = "Please enter dosage amount")
    @Size(min = 2,max = 10,message = "Your dosage amount should be at least between 2-10 chars")
    private String dosage;

    @NotNull(message = "Please enter prescription note")
    @Size(min = 1,max = 200,message = "Your prescription note should be at least between 1-200 chars")
    private String prescriptionNote;

    @NotNull(message = "Patient id is required")
    private Long patientId;

}
