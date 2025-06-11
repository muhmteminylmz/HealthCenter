package com.example.health_center.controller;

import com.example.health_center.payload.request.PatientDiseaseRequest;
import com.example.health_center.payload.response.PatientDiseaseResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.security.service.UserDetailsImpl;
import com.example.health_center.service.domain.PatientDiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/patient-diseases")
@RequiredArgsConstructor
public class PatientDiseaseController {

    private final PatientDiseaseService patientDiseaseService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public ResponseMessage<?> addDisease(@RequestBody @Valid PatientDiseaseRequest request) {
        return patientDiseaseService.addDiseaseToPatient(request);
    }

    @GetMapping("/by-patient")
    @PreAuthorize("hasAuthority('PATIENT')")
    public List<PatientDiseaseResponse> getDiseasesForPatient(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return patientDiseaseService.getDiseasesByPatientId(userDetails.getId());
    }

    /*@DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public ResponseEntity<?> deleteDisease(@PathVariable Long id) {
        return patientDiseaseService.deleteDisease(id);
    }*/

}

