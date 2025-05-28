package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.Examination;
import com.example.health_center.entity.concretes.LabTest;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.request.LabTestRequest;
import com.example.health_center.payload.response.LabTestResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.LabTestRepository;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabTestService {

    private final LabTestRepository labTestRepository;
    private final ExaminationService examinationService;


    public ResponseMessage<LabTestResponse> save(String username,LabTestRequest labTestRequest) {

        List<Examination> examinations = examinationService.getMyExaminationsByDoctorUsernameForLabTest(username);
        Examination examination = examinationService.getByIdForLabTest(labTestRequest.getExaminationId());

        if (examinations.isEmpty()){
            throw new ResourceNotFoundException(String.format(Messages.DOCTOR_HAS_NO_EXAMINATIONS_MESSAGE,username));
        }else if (examinations.stream().noneMatch(t -> t.equals(examination))) {
            throw new ResourceNotFoundException(String.format(Messages.INCORRECT_EXAMINATION_MESSAGE,username));
        }

        LabTest labTest = createLabTestRequestToDto(labTestRequest);

        labTest.setDoctor(examination.getDoctor());
        labTest.setPatient(examination.getPatient());
        labTest.setExamination(examination);

        LabTest savedLabTest = labTestRepository.save(labTest);

        examinationService.saveLabTestForExamination(savedLabTest);

        return ResponseMessage.<LabTestResponse>builder()
                .message("LabTest Created")
                .object(createLabTestResponse(savedLabTest))
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    public List<LabTestResponse> getAll() {

        return labTestRepository.findAll().stream().
                map(this::createLabTestResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<LabTestResponse> getById(Long id) {

        LabTest labTest = labTestRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException(String.format(Messages.LABTEST_NOT_FOUND_MESSAGE,id)));

        return ResponseMessage.<LabTestResponse>builder()
                .message("LabTest Found")
                .object(createLabTestResponse(labTest))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<LabTestResponse> update(Long labTestId, LabTestRequest labTestRequest) {

        LabTest labTest = labTestRepository.findById(labTestId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.LABTEST_NOT_FOUND_MESSAGE,labTestId)));

        LabTest updatedLabTest = createUpdatedLabTestResponse(labTestRequest,labTestId);


        updatedLabTest.setTestResult(labTest.getTestResult());
        updatedLabTest.setTestType(labTest.getTestType());

        updatedLabTest.setExamination(labTest.getExamination());
        updatedLabTest.setDoctor(labTest.getDoctor());
        updatedLabTest.setPatient(labTest.getPatient());

        LabTest savedLabTest = labTestRepository.save(updatedLabTest);

        return ResponseMessage.<LabTestResponse>builder()
                .message("LabTest Updated")
                .object(createLabTestResponse(savedLabTest))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<LabTestResponse> updateLabTestStatus(Long id,LabTestRequest labTestRequest) {

        LabTest labTest = labTestRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException(String.format(Messages.LABTEST_NOT_FOUND_MESSAGE,id)));

        labTest.setStatus(labTestRequest.getStatus());

        labTestRepository.save(labTest);

        return ResponseMessage.<LabTestResponse>builder()
                .message("LabTest Status Updated")
                .object(createLabTestResponse(labTest))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<?> delete(Long id) {

        labTestRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException(String.format(Messages.LABTEST_NOT_FOUND_MESSAGE,id)));

        labTestRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Deleted Successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public List<LabTestResponse> getMyLabTestsByUsernameForDoctor(String username) {

        return labTestRepository.findAllByDoctorUsername(username).stream()
                .map(this::createLabTestResponse)
                .collect(Collectors.toList());
    }

    public List<LabTestResponse> getMyLabTestsByUsernameForPatient(String username) {

        return labTestRepository.findAllByPatientUsername(username).stream()
                .map(this::createLabTestResponse)
                .collect(Collectors.toList());
    }


    public LabTest createLabTestRequestToDto(LabTestRequest labTestRequest){
        return LabTest.builder()
                .testDate(labTestRequest.getTestDate())
                .testResult(labTestRequest.getTestResult())
                .testType(labTestRequest.getTestType())
                .status(labTestRequest.getStatus())
                .price(labTestRequest.getPrice())
                .build();

    }

    public LabTestResponse createLabTestResponse(LabTest labTest){
        return LabTestResponse.builder()
                .id(labTest.getId())
                .testDate(labTest.getTestDate())
                .testResult(labTest.getTestResult())
                .testType(labTest.getTestType())
                .status(labTest.getStatus())
                .price(labTest.getPrice())
                .doctorName(labTest.getDoctor().getName())
                .patientName(labTest.getPatient().getName())
                .doctorId(labTest.getDoctor().getId())
                .patientId(labTest.getPatient().getId())
                .build();
    }

    public LabTest createUpdatedLabTestResponse(LabTestRequest newLabTest,Long labTestId){
        return LabTest.builder()
                .id(labTestId)
                .testDate(newLabTest.getTestDate())
                .testResult(newLabTest.getTestResult())
                .testType(newLabTest.getTestType())
                .status(newLabTest.getStatus())
                .price(newLabTest.getPrice())
                .build();
    }


}
