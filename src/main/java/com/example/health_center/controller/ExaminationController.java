package com.example.health_center.controller;

import com.example.health_center.entity.concretes.Appointment;
import com.example.health_center.entity.concretes.Examination;
import com.example.health_center.payload.request.AppointmentRequest;
import com.example.health_center.payload.request.ExaminationRequest;
import com.example.health_center.payload.response.ExaminationResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.AppointmentService;
import com.example.health_center.service.ExaminationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("examination")
@RequiredArgsConstructor
public class ExaminationController {

    private final ExaminationService examinationService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public ResponseMessage<ExaminationResponse> save(HttpServletRequest request,
            @RequestBody @Valid ExaminationRequest examinationRequest){

        String username = (String) request.getAttribute("username");
        return examinationService.save(username,examinationRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public List<ExaminationResponse> getAll(){
        return examinationService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<ExaminationResponse> getById(@PathVariable Long id){
        return examinationService.getById(id);
    }

    @GetMapping("/getExaminationsByDoctor")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public List<ExaminationResponse> getMyExaminationsForDoctor(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return examinationService.getMyExaminationsForDoctor(username);
    }

    @GetMapping("/getExaminationsByUserByPatient")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<ExaminationResponse> getMyExaminationsForPatient(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return examinationService.getMyExaminationsForPatient(username);
    }

    @GetMapping("/getExaminationsByUserByNurse")
    @PreAuthorize("hasAnyAuthority('NURSE')")
    public List<ExaminationResponse> getMyExaminationsForNurse(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return examinationService.getMyExaminationsForNurse(username);
    }

    @PutMapping("/update/{examinationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<ExaminationResponse> update(@PathVariable Long examinationId,
                                               @RequestBody @Valid ExaminationRequest examinationRequest){

        return examinationService.update(examinationId,examinationRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<?> delete(@PathVariable Long id){
        return examinationService.delete(id);
    }
}
