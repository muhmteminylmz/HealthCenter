package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.Allergy;
import com.example.health_center.entity.concretes.Disease;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.response.AllergyResponse;
import com.example.health_center.payload.response.AppointmentResponse;
import com.example.health_center.payload.response.DiseaseResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.DiseaseRepository;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;


/*    public List<DiseaseResponse> getByPatient(Long patientId) {

        return diseaseRepository.findAllByPatientId(patientId).stream().
                map(this::createDiseaseResponse).
                collect(Collectors.toList());
    }*/

    public List<DiseaseResponse> getAll() {

        return diseaseRepository.findAll().stream().
                map(this::createDiseaseResponse).
                collect(Collectors.toList());
    }

    public Disease getDiseaseById(Long id) {
        return diseaseRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.DISEASE_NOT_FOUND_MESSAGE,id)));
    }

    public ResponseMessage<DiseaseResponse> getById(Long id) {

        Disease disease = diseaseRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(Messages.DISEASE_NOT_FOUND_MESSAGE,id)));

        return ResponseMessage.<DiseaseResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Disease Found")
                .object(createDiseaseResponse(disease))
                .build();
    }

    public DiseaseResponse createDiseaseResponse(Disease disease){
        return DiseaseResponse.builder()
                .id(disease.getId())
                .code(disease.getCode())
                .name(disease.getName())
                .build();
    }

    public List<DiseaseResponse> createDiseaseResponseList(List<Disease> diseases) {
        return diseases.stream().map(this::createDiseaseResponse).collect(Collectors.toList());
    }

    public List<Disease> getDiseaseByDiseaseIds(List<Long> allergyIds) {

        return diseaseRepository.findAllById(allergyIds);
    }
}
