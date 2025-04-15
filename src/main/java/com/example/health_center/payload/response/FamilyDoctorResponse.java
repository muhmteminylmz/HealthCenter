package com.example.health_center.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FamilyDoctorResponse {

    private Long id;

    private Long doctorId;

    private List<Long> patientIds;
}
