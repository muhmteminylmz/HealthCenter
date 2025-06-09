package com.example.health_center.service.domain;

import com.example.health_center.controller.AllergyController;
import com.example.health_center.entity.concretes.Allergy;
import com.example.health_center.entity.concretes.Patient;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.response.AllergyResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.AllergyRepository;
import com.example.health_center.repository.PatientRepository;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AllergyService {

    private final AllergyRepository allergyRepository;
    private final PatientRepository patientRepository;

    public List<AllergyResponse> getAll() {

        return allergyRepository.findAll().stream().
                map(this::createAllergyResponse).
                collect(Collectors.toList());
    }

    public List<AllergyResponse> getByPatient(Long patientId) {

        Patient patient = patientRepository.findById(patientId).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE,patientId)));
        //ManyToMany patient ten jointable yapildigi icin patient tablosundan cekilmeli veri
        //Normalde Repo ya baska service den erismem deneme amacli

        return  patient.getAllergies().stream()
                .map(this::createAllergyResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<AllergyResponse> getById(Long id) {

        Allergy allergy = allergyRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.ALLERGY_NOT_FOUND_MESSAGE,id)));

        return ResponseMessage.<AllergyResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Allergy Found")
                .object(createAllergyResponse(allergy))
                .build();

    }

    public AllergyResponse createAllergyResponse(Allergy allergy){
        return AllergyResponse.builder()
                .id(allergy.getId())
                .code(allergy.getCode())
                .name(allergy.getName())
                .build();
    }

    public List<AllergyResponse> createAllergyResponseList(List<Allergy> allergies) {
        return allergies.stream().map(this::createAllergyResponse).collect(Collectors.toList());
    }

    public List<Allergy> getAllAllergiesByAllergyIds(List<Long> allergyIds) {
        return allergyRepository.findAllById(allergyIds);
    }
}
