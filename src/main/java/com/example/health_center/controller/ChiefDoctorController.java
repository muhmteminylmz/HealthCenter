package com.example.health_center.controller;

import com.example.health_center.entity.concretes.Appointment;
import com.example.health_center.entity.concretes.ChiefDoctor;
import com.example.health_center.payload.request.ChiefDoctorRequest;
import com.example.health_center.payload.response.ChiefDoctorResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.ChiefDoctorService;
import com.example.health_center.service.UserRoleService;
import com.example.health_center.utils.FieldControl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("chiefDoctor")
@RequiredArgsConstructor
@RestController
public class ChiefDoctorController {

    private final ChiefDoctorService chiefDoctorService;

    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;
    private final FieldControl fieldControl;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<ChiefDoctorResponse> save(@RequestBody @Valid ChiefDoctorRequest chiefDoctorRequest){

        return chiefDoctorService.save(chiefDoctorRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<ChiefDoctorResponse> getAll(){
        return chiefDoctorService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<ChiefDoctorResponse>getById(@PathVariable Long id){
        return chiefDoctorService.getById(id);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<?> update(@PathVariable Long userId,
                                               @RequestBody @Valid ChiefDoctorRequest chiefDoctorRequest){

        return chiefDoctorService.update(userId,chiefDoctorRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable Long id){
        return chiefDoctorService.delete(id);
    }
}
