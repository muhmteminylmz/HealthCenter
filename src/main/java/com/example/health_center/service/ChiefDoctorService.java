package com.example.health_center.service;

import com.example.health_center.entity.concretes.Appointment;
import com.example.health_center.entity.concretes.ChiefDoctor;
import com.example.health_center.entity.concretes.UserRole;
import com.example.health_center.entity.enums.RoleType;
import com.example.health_center.exception.ConflictException;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.request.ChiefDoctorRequest;
import com.example.health_center.payload.response.ChiefDoctorResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.ChiefDoctorRepository;
import com.example.health_center.utils.CheckParameterUpdateMethod;
import com.example.health_center.utils.FieldControl;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Encoder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChiefDoctorService {

    private final ChiefDoctorRepository chiefDoctorRepository;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    public ResponseMessage<ChiefDoctorResponse> save(ChiefDoctorRequest chiefDoctorRequest) {

        fieldControl.checkDuplicate(chiefDoctorRequest.getUsername(),chiefDoctorRequest.getTc(),
                chiefDoctorRequest.getPhoneNumber());

        ChiefDoctor chiefDoctor = createChiefDoctorRequestToDTO(chiefDoctorRequest);

        chiefDoctor.setPassword(passwordEncoder.encode(chiefDoctor.getPassword()));
        chiefDoctor.setUserRole(userRoleService.getUserRole(RoleType.CHIEF_DOCTOR));

        ChiefDoctor savedChiefDoctor = chiefDoctorRepository.save(chiefDoctor);

        return ResponseMessage.<ChiefDoctorResponse>builder()
                .httpStatus(HttpStatus.CREATED)
                .message("ChiefDoctor Created")
                .object(createChiefDoctorResponse(savedChiefDoctor))
                .build();

    }

    public List<ChiefDoctorResponse> getAll() {

        return chiefDoctorRepository.findAll().stream().
                map(this::createChiefDoctorResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<ChiefDoctorResponse> getById(Long id) {

        Optional<ChiefDoctor> chiefDoctor = chiefDoctorRepository.findById(id);

        if (!chiefDoctorRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE));
        }

        return ResponseMessage.<ChiefDoctorResponse>builder()
                .httpStatus(HttpStatus.OK)
                .object(createChiefDoctorResponse(chiefDoctor.get()))
                .build();
    }

    public ResponseMessage<ChiefDoctorResponse> update(Long userId, ChiefDoctorRequest newChiefDoctor) {

        Optional<ChiefDoctor> chiefDoctor = chiefDoctorRepository.findById(userId);

        if (chiefDoctor.isEmpty()){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,userId));
        }else if(!CheckParameterUpdateMethod.checkParameter(chiefDoctor.get(),newChiefDoctor)){
            fieldControl.checkDuplicate(newChiefDoctor.getUsername(),newChiefDoctor.getTc(),newChiefDoctor.getPhoneNumber());
        }

        ChiefDoctor updatedChiefDoctor = createUpdatedChiefDoctor(newChiefDoctor,userId);

        updatedChiefDoctor.setPassword(passwordEncoder.encode(updatedChiefDoctor.getPassword()));

        ChiefDoctor savedChiefDoctor = chiefDoctorRepository.save(updatedChiefDoctor);

        return ResponseMessage.<ChiefDoctorResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Updated Successfully")
                .object(createChiefDoctorResponse(savedChiefDoctor))
                .build();
    }

    public ResponseMessage<?> delete(Long id) {

        if (!chiefDoctorRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE));
        }

        chiefDoctorRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ChiefDoctor createChiefDoctorRequestToDTO(ChiefDoctorRequest chiefDoctorRequest){
        return ChiefDoctor.builder()
                .gender(chiefDoctorRequest.getGender())
                .surname(chiefDoctorRequest.getSurname())
                .tc(chiefDoctorRequest.getTc())
                .birthDate(chiefDoctorRequest.getBirthDate())
                .name(chiefDoctorRequest.getName())
                .dutyEndDate(chiefDoctorRequest.getDutyEndDate())
                .dutyStartDate(chiefDoctorRequest.getDutyStartDate())
                .phoneNumber(chiefDoctorRequest.getPhoneNumber())
                .username(chiefDoctorRequest.getUsername())
                .build();
    }

    public ChiefDoctorResponse createChiefDoctorResponse(ChiefDoctor chiefDoctor){
        return ChiefDoctorResponse.builder()
                .userId(chiefDoctor.getId())
                .name(chiefDoctor.getName())
                .birthDate(chiefDoctor.getBirthDate())
                .gender(chiefDoctor.getGender())
                .phoneNumber(chiefDoctor.getPhoneNumber())
                .surname(chiefDoctor.getSurname())
                .tc(chiefDoctor.getTc())
                .userName(chiefDoctor.getUsername())
                .dutyEndDate(chiefDoctor.getDutyEndDate())
                .dutyStartDate(chiefDoctor.getDutyStartDate())
                .build();
    }

    public ChiefDoctor createUpdatedChiefDoctor(ChiefDoctorRequest chiefDoctorRequest,Long id){
        return ChiefDoctor.builder()
                .gender(chiefDoctorRequest.getGender())
                .surname(chiefDoctorRequest.getSurname())
                .tc(chiefDoctorRequest.getTc())
                .birthDate(chiefDoctorRequest.getBirthDate())
                .name(chiefDoctorRequest.getName())
                .dutyEndDate(chiefDoctorRequest.getDutyEndDate())
                .dutyStartDate(chiefDoctorRequest.getDutyStartDate())
                .phoneNumber(chiefDoctorRequest.getPhoneNumber())
                .username(chiefDoctorRequest.getUsername())
                .id(id)
                .userRole(userRoleService.getUserRole(RoleType.CHIEF_DOCTOR))
                .build();
    }
}
