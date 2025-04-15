package com.example.health_center.controller;

import com.example.health_center.entity.concretes.Admin;
import com.example.health_center.payload.request.AdminRequest;
import com.example.health_center.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;


@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    public final AdminService adminService;


    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> save(@RequestBody @Valid AdminRequest adminRequest){

        return ResponseEntity.ok(adminService.save(adminRequest));
    }

    //Not: getAll() *****
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<Admin>> getAll(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type,"desc")){
            //Pageable obje olusturulmasi service katindada yapilabilir.
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        Page<Admin> author = adminService.getAllAdmin(pageable);
        return new ResponseEntity<>(author, HttpStatus.OK);
    }


    //Not: delete() *****
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id){

        return ResponseEntity.ok(adminService.deleteAdmin(id));

    }


}
