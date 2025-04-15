package com.example.health_center.service;

import com.example.health_center.entity.concretes.Prescription;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.request.PrescriptionRequest;
import com.example.health_center.payload.response.PrescriptionResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.PrescriptionRepository;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;


    public ResponseMessage<PrescriptionResponse> save(String username,PrescriptionRequest prescriptionRequest) {

        Prescription prescription = createPrescriptionRequestToDTO(prescriptionRequest);
        prescription.setFollowUpDate(LocalDate.now().plusDays(15));
        //TODO suanlik sistem otomatik atiyor ama istenirse doktor belirleyebilir veya ilac classindan sonra karar verilebilir

        prescription.setPatient(patientService.getPatientById(prescriptionRequest.getPatientId()));
        prescription.setDoctor(doctorService.getByUsername(username));

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        return ResponseMessage.<PrescriptionResponse>builder()
                .httpStatus(HttpStatus.CREATED)
                .message("Prescription created")
                .object(createPrescriptionResponse(savedPrescription))
                .build();
    }

    public List<PrescriptionResponse> getAll() {

        return prescriptionRepository.findAll().stream()
                .map(this::createPrescriptionResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<?> getById(Long id) {

        Prescription prescription = prescriptionRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_PRESCRIPTION_MESSAGE, id)));

        return ResponseMessage.<PrescriptionResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Prescription found")
                .object(createPrescriptionResponse(prescription))
                .build();
    }

    public ResponseMessage<PrescriptionResponse> update(Long prescriptionId, PrescriptionRequest prescriptionRequest) {

        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_PRESCRIPTION_MESSAGE, prescriptionId)));

        Prescription updatedPrescription = createUpdatePrescriptionResponse(prescriptionRequest,prescriptionId);
        updatedPrescription.setDoctor(prescription.getDoctor());
        updatedPrescription.setPatient(prescription.getPatient());
        updatedPrescription.setFollowUpDate(prescription.getFollowUpDate());
        //TODO dinamik bir sistem getirilebilir

        Prescription savedPrescription = prescriptionRepository.save(updatedPrescription);

        return ResponseMessage.<PrescriptionResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Prescription updated")
                .object(createPrescriptionResponse(savedPrescription))
                .build();
    }

    public ResponseMessage<?> delete(Long id) {

        prescriptionRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_PRESCRIPTION_MESSAGE, id)));

        prescriptionRepository.deleteById(id);

        return ResponseMessage.builder()
                .httpStatus(HttpStatus.OK)
                .message("Prescription deleted")
                .build();
    }

    public List<PrescriptionResponse> getMyPrescriptionsByUsernameForDoctor(String username) {

        return prescriptionRepository.findByDoctorUsername(username).stream()
                .map(this::createPrescriptionResponse)
                .collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getMyPrescriptionsByUsernameForPatient(String username) {

        return prescriptionRepository.findByPatientUsername(username).stream()
                .map(this::createPrescriptionResponse)
                .collect(Collectors.toList());
    }


    public PrescriptionResponse createPrescriptionResponse(Prescription prescription) {
        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .medicine(prescription.getMedicine())
                .dosage(prescription.getDosage())
                .prescriptionNote(prescription.getPrescriptionNote())
                .followUpDate(prescription.getFollowUpDate())
                .build();
    }

    public Prescription createPrescriptionRequestToDTO(PrescriptionRequest prescriptionRequest) {
        return Prescription.builder()
                .prescriptionNote(prescriptionRequest.getPrescriptionNote())
                .dosage(prescriptionRequest.getDosage())
                .medicine(prescriptionRequest.getMedicine())
                .build();
    }

    public Prescription createUpdatePrescriptionResponse(PrescriptionRequest newPrescription,Long prescriptionId) {
        return Prescription.builder()
                .id(prescriptionId)
                .medicine(newPrescription.getMedicine())
                .dosage(newPrescription.getDosage())
                .prescriptionNote(newPrescription.getPrescriptionNote())
                .build();
    }
}
