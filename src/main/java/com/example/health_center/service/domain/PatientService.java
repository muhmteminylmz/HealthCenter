package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.*;
import com.example.health_center.entity.enums.RoleType;
import com.example.health_center.entity.relations.PatientDisease;
import com.example.health_center.exception.ConflictException;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.request.PatientRequest;
import com.example.health_center.payload.response.PatientResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.PatientRepository;
import com.example.health_center.utils.CheckParameterUpdateMethod;
import com.example.health_center.utils.FieldControl;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final FamilyDoctorService familyDoctorService;
    private final AllergyService allergyService;
    private final MedicalReportService medicalReportService;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    public ResponseMessage<PatientResponse> save(PatientRequest patientRequest) {

        fieldControl.checkDuplicate(patientRequest.getUsername(),patientRequest.getTc(),patientRequest.getPhoneNumber());

        Patient patient = createPatientRequestToDTO(patientRequest);

        patient.setPassword(passwordEncoder.encode(patientRequest.getPassword()));

        FamilyDoctor assignedFamilyDoctor = familyDoctorService.assignFamilyDoctorToPatient(patient);
        patient.setFamilyDoctor(assignedFamilyDoctor);

        Patient savedPatient = patientRepository.save(patient);

        return ResponseMessage.<PatientResponse>builder()
                .httpStatus(HttpStatus.CREATED)
                .message("Created Successfully")
                .object(createPatientResponse(savedPatient))
                .build();
    }

    public List<PatientResponse> getAll() {

        return patientRepository.findAll().stream()
                .map(this::createPatientResponse)
                .collect(Collectors.toList());
    }

    public PatientResponse getById(Long id) {

        Patient patient = patientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,id)));

        return createPatientResponse(patient);
    }

    public ResponseMessage<Patient> update(Long userId, PatientRequest newPatient) {

        Patient patient = patientRepository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,userId)));

        if (!CheckParameterUpdateMethod.checkParameter(patient,newPatient)){
            fieldControl.checkDuplicate(newPatient.getUsername(),newPatient.getTc(),newPatient.getPhoneNumber());
        }

        Patient updatedPatient = createUpdatedPatient(newPatient,userId);

        updatedPatient.setPassword(passwordEncoder.encode(newPatient.getPassword()));
        updatedPatient.setAppointments(patient.getAppointments());
        updatedPatient.setPrescriptions(patient.getPrescriptions());
        updatedPatient.setExamination(patient.getExamination());
        updatedPatient.setFamilyDoctor(patient.getFamilyDoctor());

        return ResponseMessage.<Patient>builder()
                .httpStatus(HttpStatus.OK)
                .message("Updated Successfully")
                .object(patientRepository.save(updatedPatient))
                .build();
    }

    public ResponseMessage<?> changeFamilyDoctor(String username){

        if (!patientRepository.existsByUsername(username)){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE));
        }

        Patient patient = patientRepository.findByUsernameEquals(username);

        patient.setFamilyDoctor(familyDoctorService.assignFamilyDoctorToPatient(patient));

        patientRepository.save(patient);

        return ResponseMessage.<PatientResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Updated Successfully")
                .build();
    }


    public ResponseMessage<?> delete(Long id) {

        if (!patientRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,id));
        }

        patientRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public List<PatientResponse> getMyPatientsByUsername(String username) {

        return patientRepository.findAllByFamilyDoctorUsername(username).stream()
                .map(this::createPatientResponse).collect(Collectors.toList());
    }

    public Patient getByUsername(String username) {

        if (!patientRepository.existsByUsername(username)){
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER_MESSAGE));
        }

        return patientRepository.findByUsernameEquals(username);
    }

    public Patient getPatientById(Long patientId) {

        return patientRepository.findById(patientId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,patientId)));
    }



    public PatientResponse createPatientResponse(Patient patient){
        return PatientResponse.builder()
                .tc(patient.getTc())
                .name(patient.getName())
                .birthDate(patient.getBirthDate())
                .gender(patient.getGender())
                .phoneNumber(patient.getPhoneNumber())
                .surname(patient.getSurname())
                .userName(patient.getUsername())
                .userId(patient.getId())
                .bloodType(patient.getBloodType())
                .allergies(allergyService.createAllergyResponseList(patient.getAllergies()))
                .medicalReports(medicalReportService.getPatientReportsForCustom(patient.getId()))
                //TODO .familyDoctorId(patient.getFamilyDoctor() != null ? patient.getFamilyDoctor().getId() : null)
                .familyDoctorId(patient.getFamilyDoctor().getId())
                .build();
    }

    public Patient createPatientRequestToDTO(PatientRequest patientRequest){
        return Patient.builder()
                .gender(patientRequest.getGender())
                .surname(patientRequest.getSurname())
                .tc(patientRequest.getTc())
                .birthDate(patientRequest.getBirthDate())
                .name(patientRequest.getName())
                .bloodType(patientRequest.getBloodType())
                .phoneNumber(patientRequest.getPhoneNumber())
                .username(patientRequest.getUsername())
                .userRole(userRoleService.getUserRole(RoleType.PATIENT))
                .build();
    }

    private Patient createUpdatedPatient(PatientRequest newPatient, Long userId) {
        return Patient.builder()
                .tc(newPatient.getTc())
                .name(newPatient.getName())
                .birthDate(newPatient.getBirthDate())
                .gender(newPatient.getGender())
                .phoneNumber(newPatient.getPhoneNumber())
                .surname(newPatient.getSurname())
                .username(newPatient.getUsername())
                .id(userId)
                .bloodType(newPatient.getBloodType())
                .allergies(allergyService.getAllAllergiesByAllergyIds(newPatient.getAllergyIds()))
                .userRole(userRoleService.getUserRole(RoleType.PATIENT))
                .build();
    }

    /*public void saveDiseasesForPatient(Long patientId,List<Long> diseaseIds) {
        Patient patient = getPatientById(patientId);

        List<Disease> diseases = diseaseService.getDiseaseByDiseaseIds(diseaseIds);

        // Yeni ilişkileri oluştur
        List<PatientDisease> relations = diseases.stream()
                .map(disease -> PatientDisease.builder()
                        .patient(patient)
                        .disease(disease)
                        .diagnosedAt(LocalDate.now()) // veya dışarıdan gelen tarih
                        .build())
                .collect(Collectors.toList());

        patientDiseaseService.saveAllPatientDiseases(relations);
    }*/

    public boolean hasCommonElements(List<Long> ids,List<Long> allIds){
        return allIds.stream().noneMatch(ids::contains);
    }

    public void saveAllergiesForPatient(Long patientId,List<Long> allergyIds) {
        Patient patient = getPatientById(patientId);

        List<Long> patientAllergyIds = patient.getAllergies().stream().map(Allergy::getId).toList();

        if (!hasCommonElements(allergyIds,patientAllergyIds)){
            throw new ResourceNotFoundException(Messages.ALLERGY_DUPLICATE_MESSAGE);
        }

        patient.setAllergies(allergyService.getAllAllergiesByAllergyIds(allergyIds));
        patientRepository.save(patient);
    }

    public ResponseMessage<PatientResponse> addAllergies(Long patientId, List<Long> allergyIds) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));


        List<Allergy> allergies = allergyService.getAllAllergiesByAllergyIds(allergyIds);

        List<Allergy> existingAllergies = patient.getAllergies();
        List<String> duplicateAllergyNames = allergies.stream()
                .filter(existingAllergies::contains)
                .map(Allergy::getName)
                .toList();

        if (!duplicateAllergyNames.isEmpty()) {
            throw new ConflictException(Messages.PATIENT_ALREADY_HAS_THIS_ALLERGIES + String.join(", ", duplicateAllergyNames));
        }

        allergies.removeIf(patient.getAllergies()::contains);

        existingAllergies.addAll(allergies);

        patientRepository.save(patient);

        return ResponseMessage.<PatientResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Allergies Added")
                .object(createPatientResponse(patient))
                .build();
    }
}
