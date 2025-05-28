package com.example.health_center.controller;

import com.example.health_center.payload.response.FamilyDoctorResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.domain.FamilyDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("familyDoctor")
@RequiredArgsConstructor
public class FamilyDoctorController {

    private final FamilyDoctorService familyDoctorService;

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public List<FamilyDoctorResponse> getAll(){
        return familyDoctorService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public ResponseMessage<FamilyDoctorResponse> getById(@PathVariable @Valid Long id){
        return familyDoctorService.getById(id);
    }


    //Artik Aile Doktoru Degil
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable @Valid Long id){
        return familyDoctorService.delete(id);
    }
}
