package com.example.health_center.controller;

import com.example.health_center.payload.request.MedicalReportRequest;
import com.example.health_center.payload.response.MedicalReportResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.security.service.UserDetailsImpl;
import com.example.health_center.service.domain.MedicalReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/medical-report")
@RestController
@RequiredArgsConstructor
public class MedicalReportController {

    private final MedicalReportService medicalReportService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public ResponseMessage<?> save(@RequestBody @Valid MedicalReportRequest medicalReportRequest) {
        return medicalReportService.saveMedicalReport(medicalReportRequest);
    }

    @GetMapping("/patient/reports")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<MedicalReportResponse> getReportsForPatient(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return medicalReportService.getPatientReports(currentUser);
    }


    @GetMapping("/doctor/reports")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public List<MedicalReportResponse> getReportsForDoctor(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return medicalReportService.getDoctorReports(currentUser);
    }


    @DeleteMapping("/delete/{reportId}")
    @PreAuthorize("hasAnyAuthority('DOCTOR','ADMIN')")
    public ResponseMessage<?> delete(@PathVariable @Valid Long reportId) {
        return medicalReportService.deleteMedicalReport(reportId);
    }
}
