package com.example.health_center.controller;

import com.example.health_center.payload.response.AllergyResponse;
import com.example.health_center.payload.response.DiseaseResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.domain.DiseaseService;
import com.example.health_center.service.loader.DiseaseLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/diseases")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;


/*    @GetMapping("/patients/{patientId}/diseases")
    @PreAuthorize("hasRole('PATIENT') and #id == authentication.principal.id")
    public List<DiseaseResponse> getByPatient(@PathVariable Long patientId){
        return diseaseService.getByPatient(patientId);
    }*/

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<DiseaseResponse> getAll(){
        return diseaseService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<DiseaseResponse> getById(@PathVariable @Valid Long id){
        return diseaseService.getById(id);
    }


}
