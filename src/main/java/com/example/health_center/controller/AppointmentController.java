package com.example.health_center.controller;

import com.example.health_center.payload.request.AppointmentRequest;
import com.example.health_center.payload.response.AppointmentResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.domain.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public ResponseMessage<?> save(HttpServletRequest httpServletRequest,
            @RequestBody @Valid AppointmentRequest appointmentRequest){

        String username = (String) httpServletRequest.getAttribute("username");
        return appointmentService.save(username,appointmentRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<AppointmentResponse> getAll(){
        return appointmentService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public AppointmentResponse getById(@PathVariable @Valid Long id){
        return appointmentService.getById(id);
    }

    //Randevu guncelle eklenebilir TODO

    @GetMapping("/getAppointmentsByUserForPatient")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<AppointmentResponse> getMyAppointmentsForPatient(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return appointmentService.getAppointmentsByUsernameForPatient(username);
    }

    @GetMapping("/getAppointmentsByUserForDoctor")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public List<AppointmentResponse> getMyAppointmentsForDoctor(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        return appointmentService.getAppointmentsByUsernameForDoctor(username);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable @Valid Long id){
        return appointmentService.delete(id);
    }
}
