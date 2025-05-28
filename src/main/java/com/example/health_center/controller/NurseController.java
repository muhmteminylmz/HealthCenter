package com.example.health_center.controller;

import com.example.health_center.payload.request.NurseRequest;
import com.example.health_center.payload.response.NurseResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.service.domain.NurseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("nurse")
@RequiredArgsConstructor
public class NurseController {

    private final NurseService nurseService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public ResponseMessage<NurseResponse> save(@RequestBody @Valid NurseRequest nurseRequest){

        return nurseService.save(nurseRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public List<NurseResponse> getAll(){
        return nurseService.getAll();
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','CHIEFDOCTOR')")
    public ResponseMessage<NurseResponse> getById(@PathVariable @Valid Long id){
        return nurseService.getById(id);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public ResponseMessage<NurseResponse> update(@PathVariable @Valid Long userId,
                                               @RequestBody @Valid NurseRequest nurseRequest){

        return nurseService.update(userId,nurseRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CHIEFDOCTOR')")
    public ResponseMessage<?> delete(@PathVariable @Valid Long id){
        return nurseService.delete(id);
    }
}
