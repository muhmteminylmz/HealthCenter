package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.Nurse;
import com.example.health_center.entity.enums.RoleType;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.request.NurseRequest;
import com.example.health_center.payload.response.NurseResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.NurseRepository;
import com.example.health_center.utils.CheckParameterUpdateMethod;
import com.example.health_center.utils.FieldControl;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NurseService {

    private final NurseRepository nurseRepository;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    public ResponseMessage<NurseResponse> save(NurseRequest nurseRequest) {

        fieldControl.checkDuplicate(nurseRequest.getUsername(),nurseRequest.getTc(),nurseRequest.getPhoneNumber());

        Nurse nurse = createNurseRequestToDto(nurseRequest);
        nurse.setPassword(passwordEncoder.encode(nurseRequest.getPassword()));

        Nurse savedNurse = nurseRepository.save(nurse);

        return ResponseMessage.<NurseResponse>builder()
                .message("Nurse Created")
                .httpStatus(HttpStatus.CREATED)
                .object(createNurseResponse(savedNurse))
                .build();
    }

    public List<NurseResponse> getAll() {

        return nurseRepository.findAll().stream().
                map(this::createNurseResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<NurseResponse> getById(Long id) {

        Nurse nurse = nurseRepository.findById(id).orElseThrow(()
        -> new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,id)));

        return ResponseMessage.<NurseResponse>builder()
                .message("Nurse Found")
                .httpStatus(HttpStatus.OK)
                .object(createNurseResponse(nurse))
                .build();
    }

    public List<Nurse> getNursesByNurseIds(List<Long> nursesIds) {

        return nurseRepository.findAllById(nursesIds);
    }

    public ResponseMessage<NurseResponse> update(Long userId, NurseRequest newNurse) {

        Optional<Nurse> nurse = nurseRepository.findById(userId);

        if (nurse.isEmpty()){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,userId));
        } else if (!CheckParameterUpdateMethod.checkParameter(nurse.get(),newNurse)){
            fieldControl.checkDuplicate(newNurse.getUsername(),newNurse.getTc(),newNurse.getPhoneNumber());
        }

        Nurse updatedNurse = createUpdatedNurse(newNurse,userId);

        updatedNurse.setDutyStartDate(nurse.get().getDutyStartDate());
        updatedNurse.setPassword(passwordEncoder.encode(newNurse.getPassword()));
        updatedNurse.setExaminations(nurse.get().getExaminations());

        Nurse savedNurse = nurseRepository.save(updatedNurse);

        return ResponseMessage.<NurseResponse>builder()
                .message("Nurse Updated")
                .httpStatus(HttpStatus.OK)
                .object(createNurseResponse(savedNurse))
                .build();
    }

    public ResponseMessage<?> delete(Long id) {

        Nurse nurse = nurseRepository.findById(id).orElseThrow(()
        -> new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,id)));

        nurseRepository.delete(nurse);
        return ResponseMessage.builder()
                .message("Nurse Deleted")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public NurseResponse createNurseResponse(Nurse nurse){
        return NurseResponse.builder()
                .userId(nurse.getId())
                .dutyStartDate(nurse.getDutyStartDate())
                .dutyEndDate(nurse.getDutyEndDate())
                .startTime(nurse.getStartTime())
                .endTime(nurse.getEndTime())
                .name(nurse.getName())
                .tc(nurse.getTc())
                .name(nurse.getName())
                .birthDate(nurse.getBirthDate())
                .phoneNumber(nurse.getPhoneNumber())
                .gender(nurse.getGender())
                .userName(nurse.getUsername())
                .surname(nurse.getSurname())
                .build();
    }

    public Nurse createNurseRequestToDto(NurseRequest nurseRequest){
        return Nurse.builder()
                .gender(nurseRequest.getGender())
                .surname(nurseRequest.getSurname())
                .tc(nurseRequest.getTc())
                .birthDate(nurseRequest.getBirthDate())
                .name(nurseRequest.getName())
                .dutyEndDate(nurseRequest.getDutyEndDate())
                .dutyStartDate(nurseRequest.getDutyStartDate())
                .phoneNumber(nurseRequest.getPhoneNumber())
                .username(nurseRequest.getUsername())
                .startTime(nurseRequest.getStartTime())
                .endTime(nurseRequest.getEndTime())
                .userRole(userRoleService.getUserRole(RoleType.NURSE))
                .build();
    }

    public Nurse createUpdatedNurse(NurseRequest nurseRequest, Long id){
        return Nurse.builder()
                .id(id)
                //.dutyStartDate(nurseRequest.getDutyStartDate())
                .dutyEndDate(nurseRequest.getDutyEndDate())
                .startTime(nurseRequest.getStartTime())
                .endTime(nurseRequest.getEndTime())
                .name(nurseRequest.getName())
                .tc(nurseRequest.getTc())
                .birthDate(nurseRequest.getBirthDate())
                .phoneNumber(nurseRequest.getPhoneNumber())
                .gender(nurseRequest.getGender())
                .username(nurseRequest.getUsername())
                .surname(nurseRequest.getSurname())
                .userRole(userRoleService.getUserRole(RoleType.NURSE))
                .build();
    }

    public List<NurseResponse> createNurseResponseList(List<Nurse> nurses) {

        return nurses.stream().map(this::createNurseResponse).collect(Collectors.toList());
    }


    public List<Nurse> findRandomAvailableTwoNurse() {

        List<Nurse> nurses = nurseRepository.findTwoRandomNurse();

        if (nurses.size() < 2){
            throw new ResourceNotFoundException(Messages.NOT_ENOUGH_NURSE_MESSAGE);
        }

        return nurses;

    }
}
