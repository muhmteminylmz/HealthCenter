package com.example.health_center.payload.response;

import com.example.health_center.payload.response.abstracts.BaseUserResponse;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DoctorResponse extends BaseUserResponse {

    private boolean isFamilyDoctor;

    private LocalDate dutyStartDate;

    private LocalDate dutyEndDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private List<MedicalReportResponse> medicalReports;
}
