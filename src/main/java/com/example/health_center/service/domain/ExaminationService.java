package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.*;
import com.example.health_center.exception.ConflictException;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.request.ExaminationRequest;
import com.example.health_center.payload.response.ExaminationResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.ExaminationRepository;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final PatientDiseaseService patientDiseaseService;
    private final NurseService nurseService;
    private final AllergyService allergyService;
    private final DiseaseService diseaseService;

    public boolean hasDuplicatedValueInList(List<Long> ids){
        if (ids == null) return false;
        return ids.stream().distinct().count() != ids.size();
    }

    @Transactional
    public ResponseMessage<ExaminationResponse> save(String username,ExaminationRequest examinationRequest) {

         List<Appointment> appointments = appointmentService.getAppointmentsByUsernameForExamination(username);
         Appointment appointment = appointmentService.getAppointmentByAppointmentId(examinationRequest.getAppointmentId());

         if (appointments.isEmpty()){
             throw new ResourceNotFoundException(String.format(Messages.APPOINTMENT_NOT_HAS_MESSAGE,username));
         } else if (appointments.stream().noneMatch(t -> t.equals(appointment))) {
             throw new ResourceNotFoundException(String.format(Messages.INCORRECT_APPOINTMENT_MESSAGE,username));
         }


        if (hasDuplicatedValueInList(examinationRequest.getAllergy_Ids()) ||
                hasDuplicatedValueInList(examinationRequest.getDisease_Ids())){
            throw new ConflictException(Messages.ALLERGY_DISEASE_DUPLICATE_MESSAGE);
        }

        Examination examination = createExaminationRequestToDto(examinationRequest);

        Patient patient = appointment.getPatient();

        patientDiseaseService.saveDiseasesForPatient(patient.getId(),examinationRequest.getDisease_Ids());
        patientService.saveAllergiesForPatient(patient.getId(),examinationRequest.getAllergy_Ids());

         //Suanlik burdalar
        examination.setDoctor(doctorService.findRandomAvailableDoctor());
        examination.setNurses(nurseService.findRandomAvailableTwoNurse());
        examination.setPatient(patient);
        examination.setAppointment(appointment);
        //TODO

        //labTest sonra doktor gerekli gorurse update ile ekleyebilir
        //diagnosis ilk giriste girilemez sonra guncellenebilir
        //(muayne sonucu guncelleyebilir veya test yapilirsa test sonucu)

        Examination savedExamination = examinationRepository.save(examination);

        appointmentService.saveExaminationForAppointment(savedExamination);


        return ResponseMessage.<ExaminationResponse>builder()
                .message("Examination Created")
                .httpStatus(HttpStatus.CREATED)
                .object(createExaminationResponse(savedExamination))
                .build();
    }


    public List<ExaminationResponse> getAll() {

        return examinationRepository.findAll().stream().
                map(this::createExaminationResponse)
                .collect(Collectors.toList());
    }

    public List<ExaminationResponse> getByDate(LocalDate date) {

        return examinationRepository.findByExaminationDateEquals(date).stream()
                .map(this::createExaminationResponse)
                .collect(Collectors.toList());

    }

    public ResponseMessage<ExaminationResponse> getById(Long id) {

        Examination examination = examinationRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException(String.format(Messages.EXAMINATION_NOT_FOUND_MESSAGE,id)));

        return ResponseMessage.<ExaminationResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Examination Found")
                .object(createExaminationResponse(examination))
                .build();
    }

    @Transactional
    public ResponseMessage<ExaminationResponse> update(Long examinationId, ExaminationRequest examinationRequest) {

        Examination examination = examinationRepository.findById(examinationId).orElseThrow(()
                -> new ResourceNotFoundException(String.format(Messages.EXAMINATION_NOT_EXIST_MESSAGE,examinationId)));

        Examination updatedExamination = createUpdateExaminationResponse(examinationRequest, examinationId);

        Patient patient = examination.getPatient();
        patientDiseaseService.saveDiseasesForPatient(patient.getId(),examinationRequest.getDisease_Ids());
        patientService.saveAllergiesForPatient(patient.getId(),examinationRequest.getAllergy_Ids());

        updatedExamination.setAppointment(examination.getAppointment());
        updatedExamination.setExaminationDate(examination.getExaminationDate());

        updatedExamination.setNurses(examination.getNurses());
        updatedExamination.setPatient(patient);
        updatedExamination.setDoctor(examination.getDoctor());

        Examination savedExamination = examinationRepository.save(updatedExamination);

        return ResponseMessage.<ExaminationResponse>builder()
                .message("Examination Updated")
                .httpStatus(HttpStatus.OK)
                .object(createExaminationResponse(savedExamination))
                .build();
    }

    public ResponseMessage<?> delete(Long id) {

        examinationRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException(String.format(Messages.EXAMINATION_NOT_EXIST_MESSAGE,id)));

        examinationRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public List<ExaminationResponse> getMyExaminationsForDoctor(String username) {

        return examinationRepository.findAllByDoctorUsername(username).stream()
                .map(this::createExaminationResponse)
                .collect(Collectors.toList());
    }

    public List<ExaminationResponse> getMyExaminationsForPatient(String username) {

        return examinationRepository.findAllByPatientUsername(username).stream()
                .map(this::createExaminationResponse)
                .collect(Collectors.toList());
    }

    public List<ExaminationResponse> getMyExaminationsForNurse(String username) {

        return examinationRepository.findAllByNurseUsername(username).stream()
                .map(this::createExaminationResponse)
                .collect(Collectors.toList());
    }

    public List<Examination> getMyExaminationsByDoctorUsernameForLabTest(String username) {

        return new ArrayList<>(examinationRepository.findAllByDoctorUsername(username));
    }


    public ExaminationResponse createExaminationResponse(Examination examination){
        return ExaminationResponse.builder()
                .id(examination.getId())
                .examinationDate(examination.getExaminationDate())
                .allergies(allergyService.createAllergyResponseList(examination.getAllergies()))
                .diseases(diseaseService.createDiseaseResponseList(examination.getDiseases()))
                .diagnosis(examination.getDiagnosis())
                .doctors(doctorService.createDoctorResponse(examination.getDoctor()))
                .patient(patientService.createPatientResponse(examination.getPatient()))
                .nurses(nurseService.createNurseResponseList(examination.getNurses()))
                .appointment(appointmentService.createAppointmentResponse(examination.getAppointment()))
                .build();
    }

    public Examination createExaminationRequestToDto(ExaminationRequest examinationRequest){

        List<Allergy> allergies = allergyService.getAllAllergiesByAllergyIds(examinationRequest.getAllergy_Ids());
        List<Disease> diseases = diseaseService.getDiseaseByDiseaseIds(examinationRequest.getDisease_Ids());

        return Examination.builder()
                .examinationDate(examinationRequest.getExaminationDate())
                .allergies(allergies)
                .diseases(diseases)
                .diagnosis(examinationRequest.getDiagnosis())
                .appointment(appointmentService.getAppointmentByAppointmentId(examinationRequest.getAppointmentId()))
                .build();
    }

    public Examination createUpdateExaminationResponse(ExaminationRequest newExamination,Long ExaminationId){

        List<Allergy> allergies = allergyService.getAllAllergiesByAllergyIds(newExamination.getAllergy_Ids());
        List<Disease> diseases = diseaseService.getDiseaseByDiseaseIds(newExamination.getDisease_Ids());

        return Examination.builder()
                .id(ExaminationId)
                //.examinationDate(newExamination.getExaminationDate())
                .diseases(diseases)
                .allergies(allergies)
                .diagnosis(newExamination.getDiagnosis())
                //.appointment(appointmentService.getAppointmentByAppointmentId(newExamination.getAppointmentId()))
                //NOT: Bunlarin alinmasinin mantigi yok
                .build();
    }

    public Examination getByIdForLabTest(Long examinationId) {

        return examinationRepository.findById(examinationId).orElseThrow(()
                -> new ResourceNotFoundException(String.format(Messages.EXAMINATION_NOT_FOUND_MESSAGE, examinationId)));
    }

    public void saveLabTestForExamination(LabTest savedLabTest) {

        Examination examination = getByIdForLabTest(savedLabTest.getExamination().getId());

        List<LabTest> labTests = examination.getLabTests();
        if (labTests == null) {
            labTests = new ArrayList<>();
        } else {
            labTests = new ArrayList<>(labTests); //degistirilemez listeler icin
        }

        labTests.add(savedLabTest);
        examination.setLabTests(labTests);

        examinationRepository.save(examination);

    }
}
