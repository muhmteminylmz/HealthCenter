package com.example.health_center.controller;

import com.example.health_center.entity.concretes.Appointment;
import com.example.health_center.entity.concretes.LabTest;
import com.example.health_center.payload.request.AppointmentRequest;
import com.example.health_center.payload.request.LabTestRequest;
import com.example.health_center.payload.response.LabTestResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.AppointmentService;
import com.example.health_center.service.LabTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("labTest")
@RequiredArgsConstructor
public class LabTestController {

    private final LabTestService labTestService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public ResponseMessage<LabTestResponse> save(HttpServletRequest request,@RequestBody @Valid LabTestRequest labTestRequest){

        String username = (String) request.getAttribute("username");
        return labTestService.save(username,labTestRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public List<LabTestResponse> getAll(){
        return labTestService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public ResponseMessage<?> getById(@PathVariable Long id){
        return labTestService.getById(id);
    }

    @GetMapping("/getLabTestsByUsernameForDoctor")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public List<LabTestResponse> getMyLabTestsByUsernameForDoctor(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return labTestService.getMyLabTestsByUsernameForDoctor(username);
    }

    @GetMapping("/getLabTestsByUsernameForPatient")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<LabTestResponse> getMyLabTestsByUsernameForPatient(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return labTestService.getMyLabTestsByUsernameForPatient(username);
    }


    //TODO PATCH yapilip ayri DTO olusturulacak
    @PutMapping("/updateStatus/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<LabTestResponse> updateStatus(@PathVariable Long id,
                                                         @RequestBody @Valid LabTestRequest labtestRequest){
        return labTestService.updateLabTestStatus(id,labtestRequest);
    }

    @PutMapping("/update/{labTestId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<LabTestResponse> update(@PathVariable Long labTestId,
                                               @RequestBody @Valid LabTestRequest labTestRequest){

        return labTestService.update(labTestId,labTestRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR')")
    public ResponseMessage<?> delete(@PathVariable Long id){
        return labTestService.delete(id);
    }
}
