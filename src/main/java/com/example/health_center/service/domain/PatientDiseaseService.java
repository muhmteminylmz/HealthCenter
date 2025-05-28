package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.Disease;
import com.example.health_center.entity.concretes.Patient;
import com.example.health_center.entity.relations.PatientDisease;
import com.example.health_center.exception.ConflictException;
import com.example.health_center.payload.request.PatientDiseaseRequest;
import com.example.health_center.payload.response.DiseaseResponse;
import com.example.health_center.payload.response.PatientDiseaseResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.PatientDiseaseRepository;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientDiseaseService {

    private final PatientDiseaseRepository patientDiseaseRepository;

    private final PatientService patientService;

    private final DiseaseService diseaseService;

    public ResponseMessage<PatientDiseaseResponse> addDiseaseToPatient(PatientDiseaseRequest request) {
        Patient patient = patientService.getPatientById(request.getPatientId());

        Disease disease = diseaseService.getDiseaseById(request.getDiseaseId());

        boolean alreadyExists = patientDiseaseRepository.existsByPatientAndDisease(patient, disease);
        if (alreadyExists) {
            throw new ConflictException(Messages.DISEASE_ALREADY_ADDED);
        }

        PatientDisease patientDisease = createPatientDiseaseRequestToDto(request,patient,disease);

        PatientDisease savedPatientDisease = patientDiseaseRepository.save(patientDisease);

        return ResponseMessage.<PatientDiseaseResponse>builder()
                .message("Disease added successfully")
                .object(createPatientDiseaseResponse(savedPatientDisease))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //PatientService icin method
    public void saveDiseasesForPatient(Long patientId,List<Long> diseaseIds) {
        Patient patient = patientService.getPatientById(patientId);

        List<Disease> diseases = diseaseService.getDiseaseByDiseaseIds(diseaseIds);

        // Yeni ilişkileri oluştur
        List<PatientDisease> relations = diseases.stream()
                .map(disease -> PatientDisease.builder()
                        .patient(patient)
                        .disease(disease)
                        .diagnosedAt(LocalDate.now()) // veya dışarıdan gelen tarih
                        .build())
                .collect(Collectors.toList());

        patientDiseaseRepository.saveAll(relations);
    }

    private PatientDiseaseResponse createPatientDiseaseResponse(PatientDisease pd) {
        Disease disease = pd.getDisease();

        DiseaseResponse diseaseResponse = DiseaseResponse.builder()
                .id(disease.getId())
                .code(disease.getCode())
                .name(disease.getName())
                .build();

        return PatientDiseaseResponse.builder()
                .id(pd.getId())
                .disease(diseaseResponse)
                .diagnosisDate(pd.getDiagnosedAt())
                .build();
    }

    private PatientDisease createPatientDiseaseRequestToDto(PatientDiseaseRequest request,Patient patient,Disease disease) {
        return PatientDisease.builder()
                .patient(patient)
                .disease(disease)
                .diagnosedAt(request.getDiagnosisDate())
                .build();
    }

    public List<PatientDiseaseResponse> getDiseasesByPatientId(Long patientId) {
        Patient patient = patientService.getPatientById(patientId);

        List<PatientDisease> patientDiseases = patientDiseaseRepository.findAllByPatient(patient);

        return patientDiseases.stream()
                .map(this::createPatientDiseaseResponse)
                .collect(Collectors.toList());
    }

/*
    public void saveAllPatientDiseases(List<PatientDisease> relations) {
        patientDiseaseRepository.saveAll(relations);
    }
*/
}

