package com.example.health_center.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PrescriptionResponse {

    private Long id;
    private String medicine;
    private String dosage;
    private String prescriptionNote;
    private LocalDate followUpDate;

    private Long doctorId;
    private String doctorName;

    private Long patientId;
    private String patientName;

}
