package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.MedicalReport;
import com.example.health_center.entity.concretes.Patient;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.mapper.MedicalReportMapper;
import com.example.health_center.payload.request.MedicalReportRequest;
import com.example.health_center.payload.response.MedicalReportResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.MedicalReportRepository;
import com.example.health_center.security.service.UserDetailsImpl;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalReportService {

    private final MedicalReportRepository medicalReportRepository;
    private final MedicalReportMapper medicalReportMapper;
    /*private final PatientService patientService;
    private final DoctorService doctorService;
    private final DiseaseService diseaseService;*/


    public ResponseMessage<?> saveMedicalReport(MedicalReportRequest medicalReportRequest) {

        MedicalReport medicalReport = medicalReportMapper.createMedicalReportRequest(medicalReportRequest);

        MedicalReport savedMedicalReport = medicalReportRepository.save(medicalReport);

        return ResponseMessage.<MedicalReportResponse>builder()
                .object(medicalReportMapper.createMedicalReportResponse(savedMedicalReport))
                .message("Medical Report Created")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public List<MedicalReportResponse> getPatientReports(UserDetailsImpl currentUser) {

        return medicalReportRepository.findAllByPatientId(currentUser.getId())
                .stream().map(medicalReportMapper::createMedicalReportResponse)
                .toList();
    }

    public List<MedicalReportResponse> getDoctorReports(UserDetailsImpl currentUser) {
        return medicalReportRepository.findAllByDoctorId(currentUser.getId())
                .stream().map(medicalReportMapper::createMedicalReportResponse)
                .toList();
    }

    public ResponseMessage<?> deleteMedicalReport(Long reportId) {

        MedicalReport medicalReport = medicalReportRepository.findById(reportId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_MEDICAL_REPORT, reportId)));

        medicalReportRepository.delete(medicalReport);

        return ResponseMessage.<MedicalReportResponse>builder()
                .message("Medical Report Deleted")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public List<MedicalReportResponse> getPatientReportsForCustom(Long patientId) {

        return medicalReportRepository.findAllByPatientId(patientId)
                .stream().map(medicalReportMapper::createMedicalReportResponse)
                .toList();
    }

    public List<MedicalReportResponse> getDoctorReportsForCustom(Long doctorId) {

        return medicalReportRepository.findAllByDoctorId(doctorId)
                .stream().map(medicalReportMapper::createMedicalReportResponse)
                .toList();
    }


    /*public MedicalReportResponse createMedicalReportResponse(MedicalReport medicalReport){
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
    }*/



}
