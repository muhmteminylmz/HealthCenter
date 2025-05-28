package com.example.health_center.controller;

import com.example.health_center.payload.response.AllergyResponse;
import com.example.health_center.payload.response.AppointmentResponse;
import com.example.health_center.payload.response.DiseaseResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.domain.AllergyService;
import com.example.health_center.service.loader.AllergyLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @GetMapping("/getByPatient/{patientId}")
    @PreAuthorize("#patientId == authentication.principal.id and hasRole('PATIENT')")
    public List<AllergyResponse> getByPatient(@PathVariable("patientId") @Valid Long patientId){
        return allergyService.getByPatient(patientId);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<AllergyResponse> getAll(){
        return allergyService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<AllergyResponse> getById(@PathVariable @Valid Long id){
        return allergyService.getById(id);
    }
}
