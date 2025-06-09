package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.Doctor;
import com.example.health_center.entity.enums.RoleType;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.request.DoctorRequest;
import com.example.health_center.payload.response.DoctorResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.DoctorRepository;
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
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final FamilyDoctorService familyDoctorService;
    //private final MedicalReportService medicalReportService;

    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    public ResponseMessage<DoctorResponse> save(DoctorRequest doctorRequest) {

        fieldControl.checkDuplicate(doctorRequest.getUsername(),doctorRequest.getTc(),doctorRequest.getPhoneNumber());

        Doctor doctor = createDoctorRequestToDTO(doctorRequest);

        doctor.setPassword(passwordEncoder.encode(doctorRequest.getPassword()));

        Doctor savedDoctor = doctorRepository.save(doctor);

        if (doctorRequest.isFamilyDoctor()){
            familyDoctorService.saveFamilyDoctor(savedDoctor);
        }

        return ResponseMessage.<DoctorResponse>builder()
                .httpStatus(HttpStatus.CREATED)
                .message("Created Successfully")
                .object(createDoctorResponse(savedDoctor))
                .build();
    }

    public List<DoctorResponse> getAll() {

        return doctorRepository.findAll().stream().
                map(this::createDoctorResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<DoctorResponse> getById(Long id) {

        Doctor doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE)));

        return ResponseMessage.<DoctorResponse>builder()
                .httpStatus(HttpStatus.OK)
                .object(createDoctorResponse(doctor))
                .build();
    }

    public Doctor getByUsername(String username) {

        return doctorRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE)));
    }

    public ResponseMessage<DoctorResponse> getDoctorByUsername(String username) {

        Doctor doctor = doctorRepository.getDoctorByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE)));

        return ResponseMessage.<DoctorResponse>builder()
                .httpStatus(HttpStatus.OK)
                .object(createDoctorResponse(doctor))
                .message("Doctor Found")
                .build();
    }

    public ResponseMessage<DoctorResponse> update(Long userId, DoctorRequest newDoctor) {

        Optional<Doctor> doctor = doctorRepository.findById(userId);

        if (doctor.isEmpty()) {
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, userId));
        } else if (!CheckParameterUpdateMethod.checkParameter(doctor.get(), newDoctor)) {
            fieldControl.checkDuplicate(newDoctor.getUsername(), newDoctor.getTc(), newDoctor.getPhoneNumber());
        }

        Doctor updatedDoctor = createUpdatedDoctor(newDoctor,userId);

        updatedDoctor.setPassword(passwordEncoder.encode(newDoctor.getPassword()));

        Doctor savedDoctor = doctorRepository.save(updatedDoctor);

        if(!newDoctor.isFamilyDoctor() && doctor.get().isFamilyDoctor()){
            familyDoctorService.deleteByDoctorId(doctor.get().getId());
        } else if (newDoctor.isFamilyDoctor()) {
            familyDoctorService.saveFamilyDoctor(savedDoctor);
        }

        return ResponseMessage.<DoctorResponse>builder()
                .object(createDoctorResponse(savedDoctor))
                .message("Updated Successfully")
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public ResponseMessage<?> delete(Long id) {

        Doctor doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, id)));

        doctorRepository.delete(doctor);

        return ResponseMessage.builder()
                .message("Deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public DoctorResponse createDoctorResponse(Doctor doctor){
        return DoctorResponse.builder()
                .dutyStartDate(doctor.getDutyStartDate())
                .tc(doctor.getTc())
                .name(doctor.getName())
                .birthDate(doctor.getBirthDate())
                .dutyEndDate(doctor.getDutyEndDate())
                .endTime(doctor.getEndTime())
                .gender(doctor.getGender())
                .phoneNumber(doctor.getPhoneNumber())
                .startTime(doctor.getStartTime())
                .surname(doctor.getSurname())
                .userName(doctor.getUsername())
                .userId(doctor.getId())
                .isFamilyDoctor(doctor.isFamilyDoctor())
                //.medicalReports(medicalReportService.getDoctorReportsForCustom(doctor.getId()))
                .build();
    }

    public Doctor createDoctorRequestToDTO(DoctorRequest doctorRequest){
        return Doctor.builder()
                .gender(doctorRequest.getGender())
                .surname(doctorRequest.getSurname())
                .tc(doctorRequest.getTc())
                .birthDate(doctorRequest.getBirthDate())
                .name(doctorRequest.getName())
                .dutyEndDate(doctorRequest.getDutyEndDate())
                .dutyStartDate(doctorRequest.getDutyStartDate())
                .phoneNumber(doctorRequest.getPhoneNumber())
                .username(doctorRequest.getUsername())
                .startTime(doctorRequest.getStartTime())
                .endTime(doctorRequest.getEndTime())
                .isFamilyDoctor(doctorRequest.isFamilyDoctor())
                .userRole(userRoleService.getUserRole(RoleType.DOCTOR))
                .build();
    }

    public Doctor createUpdatedDoctor(DoctorRequest doctorRequest, Long id){
        return Doctor.builder()
                .gender(doctorRequest.getGender())
                .surname(doctorRequest.getSurname())
                .tc(doctorRequest.getTc())
                .birthDate(doctorRequest.getBirthDate())
                .name(doctorRequest.getName())
                //.appointment(doctorRequest.get)
                .dutyEndDate(doctorRequest.getDutyEndDate())
                .dutyStartDate(doctorRequest.getDutyStartDate())
                .phoneNumber(doctorRequest.getPhoneNumber())
                .username(doctorRequest.getUsername())
                .isFamilyDoctor(doctorRequest.isFamilyDoctor())
                .id(id)
                .startTime(doctorRequest.getStartTime())
                .endTime(doctorRequest.getEndTime())
                .medicalReports(getByIdForCustom(id).getMedicalReports())
                .userRole(userRoleService.getUserRole(RoleType.DOCTOR))
                .build();
    }

    public Doctor getByIdForCustom(Long doctorId) {
        return doctorRepository.findById(doctorId).orElseThrow(()
                -> new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,doctorId)));
    }

    public List<DoctorResponse> createDoctorResponseList(List<Doctor> doctors) {

        return doctors.stream().map(this::createDoctorResponse).collect(Collectors.toList());
    }


    public Doctor findRandomAvailableDoctor() {

        return doctorRepository.findRandomDoctor().orElseThrow(()
        -> new ResourceNotFoundException(Messages.DOCTOR_NOT_FOUND_MESSAGE));
    }

    public DoctorResponse getDoctorResponseById(Long id) {

        Doctor doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE)));
        return createDoctorResponse(doctor);
    }
}
