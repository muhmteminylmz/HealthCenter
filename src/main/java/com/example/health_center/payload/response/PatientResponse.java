package com.example.health_center.payload.response;

import com.example.health_center.entity.concretes.Allergy;
import com.example.health_center.entity.concretes.MedicalReport;
import com.example.health_center.entity.enums.BloodType;
import com.example.health_center.payload.response.abstracts.BaseUserResponse;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PatientResponse extends BaseUserResponse {

    private BloodType bloodType;

    private Long familyDoctorId;

    private List<AllergyResponse> allergies;

    private List<MedicalReportResponse> medicalReports;
}
