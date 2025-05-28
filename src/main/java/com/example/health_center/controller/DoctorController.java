package com.example.health_center.controller;

import com.example.health_center.payload.request.DoctorRequest;
import com.example.health_center.payload.response.DoctorResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.domain.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<DoctorResponse> save(@RequestBody @Valid DoctorRequest doctorRequest){

        return doctorService.save(doctorRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public List<DoctorResponse> getAll(){
        return doctorService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public ResponseMessage<?> getById(@PathVariable @Valid Long id){
        return doctorService.getById(id);
    }

    //TODO anlamsiz
    @GetMapping("/getByUsername")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public ResponseMessage<DoctorResponse> getByUsername(@RequestParam @Valid String username){
        return doctorService.getDoctorByUsername(username);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<DoctorResponse> update(@PathVariable Long userId,
                                       @RequestBody @Valid DoctorRequest doctorRequest){

        return doctorService.update(userId,doctorRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable @Valid Long id){
        return doctorService.delete(id);
    }
}
