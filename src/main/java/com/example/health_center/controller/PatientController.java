package com.example.health_center.controller;

import com.example.health_center.entity.concretes.Appointment;
import com.example.health_center.entity.concretes.Patient;
import com.example.health_center.payload.request.NurseRequest;
import com.example.health_center.payload.request.PatientRequest;
import com.example.health_center.payload.response.PatientResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.PatientService;
import com.example.health_center.service.UserRoleService;
import com.example.health_center.utils.FieldControl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;
    private final FieldControl fieldControl;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<PatientResponse> save(@RequestBody @Valid PatientRequest patientRequest){

        return patientService.save(patientRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public List<PatientResponse> getAll(){
        return patientService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public PatientResponse getById(@PathVariable Long id){
        return patientService.getById(id);
    }

    //todo sadece familydoctor kullancak
    @GetMapping("/getMyPatientsByUser")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public List<PatientResponse> getMyPatients(HttpServletRequest httpServletRequest){
        String username = (String) httpServletRequest.getAttribute("username");
        return patientService.getMyPatientsByUsername(username);
    }

    @PutMapping("/changeFamilyDoctor")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public ResponseMessage<?> changePatientFamilyDoctor(HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        return patientService.changeFamilyDoctor(username);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<Patient> update(@PathVariable Long userId,
                                           @RequestBody @Valid PatientRequest patientRequest){

        return patientService.update(userId,patientRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable Long id){
        return patientService.delete(id);
    }
}
