package com.example.health_center.controller;

import com.example.health_center.entity.concretes.Appointment;
import com.example.health_center.entity.concretes.Prescription;
import com.example.health_center.payload.request.AppointmentRequest;
import com.example.health_center.payload.request.PrescriptionRequest;
import com.example.health_center.payload.response.PrescriptionResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.AppointmentService;
import com.example.health_center.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("prescription")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR')")
    public ResponseMessage<PrescriptionResponse> save(HttpServletRequest request,@RequestBody @Valid PrescriptionRequest prescriptionRequest){

        String username = (String) request.getAttribute("username");
        return prescriptionService.save(username,prescriptionRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public List<PrescriptionResponse> getAll(){
        return prescriptionService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<?> getById(@PathVariable Long id){
        return prescriptionService.getById(id);
    }

    @GetMapping("/getPrescriptionsByDoctor")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public List<PrescriptionResponse> getMyPrescriptionsForDoctor(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return prescriptionService.getMyPrescriptionsByUsernameForDoctor(username);
    }

    @GetMapping("/getPrescriptionsByPatient")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<PrescriptionResponse> getMyPrescriptionsForPatient(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return prescriptionService.getMyPrescriptionsByUsernameForPatient(username);
    }

    @PutMapping("/update/{prescriptionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<PrescriptionResponse> update(@PathVariable Long prescriptionId,
                                               @RequestBody @Valid PrescriptionRequest prescriptionRequest){

        return prescriptionService.update(prescriptionId,prescriptionRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<?> delete(@PathVariable Long id){
        return prescriptionService.delete(id);
    }
}
