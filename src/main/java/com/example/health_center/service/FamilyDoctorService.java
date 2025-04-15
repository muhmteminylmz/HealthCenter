package com.example.health_center.service;

import com.example.health_center.entity.abstracts.User;
import com.example.health_center.entity.concretes.Doctor;
import com.example.health_center.entity.concretes.FamilyDoctor;
import com.example.health_center.entity.concretes.Patient;
import com.example.health_center.entity.enums.RoleType;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.response.FamilyDoctorResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.FamilyDoctorRepository;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyDoctorService {

    private final FamilyDoctorRepository familyDoctorRepository;
    private final UserRoleService userRoleService;


    public List<FamilyDoctorResponse> getAll() {

        return familyDoctorRepository.findAll().stream().
                map(this::createFamilyDoctorResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<FamilyDoctorResponse> getById(Long id) {

        FamilyDoctor familyDoctor = familyDoctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE)));

        return ResponseMessage.<FamilyDoctorResponse>builder()
                .httpStatus(HttpStatus.OK)
                .object(createFamilyDoctorResponse(familyDoctor))
                .build();
    }

    public void deleteByDoctorId(Long id) {

        familyDoctorRepository.findByDoctorId(id);
    }

    public ResponseMessage<?> delete(Long id) {

        familyDoctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, id)));

        familyDoctorRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public void saveFamilyDoctor(Doctor doctor){
        FamilyDoctor familyDoctor = FamilyDoctor.builder()
                .doctor(doctor)
                .userRole(userRoleService.getUserRole(RoleType.FAMILY_DOCTOR))
                .build();

        familyDoctorRepository.save(familyDoctor);
    }

    public FamilyDoctorResponse createFamilyDoctorResponse(FamilyDoctor familyDoctor){
        return FamilyDoctorResponse.builder()
                .id(familyDoctor.getId())
                .doctorId(familyDoctor.getDoctor().getId())
                .patientIds(familyDoctor.getPatients().stream().map(User::getId).collect(Collectors.toList()))
                .build();
    }


    //method for PatientService
    public FamilyDoctor assignFamilyDoctorToPatient(Patient patient) {
        Pageable top2 = PageRequest.of(0, 2);
        List<FamilyDoctor> familyDoctors = familyDoctorRepository.findTop2FamilyDoctorByPatientCount(top2);

        if (familyDoctors.isEmpty()) {
            throw new ResourceNotFoundException(Messages.NOT_FOUND_FAMILYDOCTOR_FOR_ASSIGN);
        }

        if (patient.getFamilyDoctor() == null) {
            return familyDoctors.get(0);
        }

        FamilyDoctor mostAvailableDoctor = familyDoctors.get(0);

        if (patient.getFamilyDoctor().getId().equals(mostAvailableDoctor.getId())) {
            if (familyDoctors.size() > 1) {
                return familyDoctors.get(1);
            } else {
                throw new ResourceNotFoundException(Messages.NOT_FOUND_ANOTHER_FAMILYDOCTOR_TO_ASSIGN);
            }
        }

        return mostAvailableDoctor;
    }

}
