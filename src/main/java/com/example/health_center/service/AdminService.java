package com.example.health_center.service;

import com.example.health_center.payload.request.AdminRequest;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.AdminRepository;
import com.example.health_center.entity.concretes.Admin;
import com.example.health_center.entity.enums.RoleType;
import com.example.health_center.exception.ConflictException;
import com.example.health_center.payload.request.AdminRequest;
import com.example.health_center.payload.response.AdminResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.*;
import com.example.health_center.utils.FieldControl;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final FieldControl fieldControl;

    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public ResponseMessage save(AdminRequest request) {

        fieldControl.checkDuplicate(request.getUsername(),
                                    request.getTc(),
                                    request.getPhoneNumber());

        Admin admin = createAdminForSave(request);
        admin.setBuilt_in(false);

        if(Objects.equals(request.getUsername(),"Admin")) admin.setBuilt_in(true);

        admin.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedData = adminRepository.save(admin);

        return ResponseMessage.<AdminResponse>builder().
                message("Admin saved").
                httpStatus(HttpStatus.CREATED).
                object(createResponse(savedData)). //pojo - dto
                build();
    }

    protected Admin createAdminForSave(AdminRequest request){
        return Admin.builder().
                username(request.getUsername()).
                name(request.getName()).
                surname(request.getSurname()).
                password(request.getPassword()).
                tc(request.getTc()).
                birthDate(request.getBirthDate()).
                phoneNumber(request.getPhoneNumber()).
                gender(request.getGender()).
                build();
    }

    private AdminResponse createResponse(Admin admin){
        return AdminResponse.builder().
                userId(admin.getId()).
                userName(admin.getUsername()).
                name(admin.getName()).
                surname(admin.getSurname()).
                birthDate(admin.getBirthDate()).
                tc(admin.getTc()).
                phoneNumber(admin.getPhoneNumber()).
                gender(admin.getGender()).
                build();
    }

    //Not: getAll() ****
    public Page<Admin> getAllAdmin(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }


    //Not: delete() ****
    public String deleteAdmin(Long id) {

        Optional<Admin> admin = adminRepository.findById(id);

        if(admin.isPresent() && admin.get().isBuilt_in()){
            throw new ConflictException(Messages.NOT_PERMITTED_METHOD_MESSAGE);
        }

        if(admin.isPresent()){
            adminRepository.deleteById(id);

            return "Admin is deleted Successfully";
        }

        return Messages.NOT_FOUND_USER_MESSAGE;
    }

    //runner tarafi icin yazildi
    public long countAllAdmin() {
        return adminRepository.count();
    }
}