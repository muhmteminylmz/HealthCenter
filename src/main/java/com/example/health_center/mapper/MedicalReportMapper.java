package com.example.health_center.mapper;

import com.example.health_center.entity.concretes.MedicalReport;
import com.example.health_center.payload.request.MedicalReportRequest;
import com.example.health_center.payload.response.MedicalReportResponse;
import com.example.health_center.service.domain.DoctorService;
import com.example.health_center.service.domain.DiseaseService;
import com.example.health_center.service.domain.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalReportMapper {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DiseaseService diseaseService;

    public MedicalReportResponse createMedicalReportResponse(MedicalReport medicalReport){
        return MedicalReportResponse.builder()
                .id(medicalReport.getId())
                .description(medicalReport.getDescription())
                .patient(patientService.getById(medicalReport.getPatient().getId()))
                .doctor(doctorService.getDoctorResponseById(medicalReport.getDoctor().getId()))
                .name(medicalReport.getName())
                .diagnosis(diseaseService.createDiseaseResponse(medicalReport.getDiagnosis()))
                .endDate(medicalReport.getEndDate())
                .startDate(medicalReport.getStartDate())
                .build();
    }

    public MedicalReport createMedicalReportRequest(MedicalReportRequest medicalReportRequest){
        return MedicalReport.builder()
                .description(medicalReportRequest.getDescription())
                .name(medicalReportRequest.getName())
                .diagnosis(diseaseService.getDiseaseById(medicalReportRequest.getDiagnosisId()))
                .endDate(medicalReportRequest.getEndDate())
                .startDate(medicalReportRequest.getStartDate())
                .patient(patientService.getPatientById(medicalReportRequest.getPatientId()))
                .doctor(doctorService.getByIdForCustom(medicalReportRequest.getDoctorId()))
                .build();
    }
}
