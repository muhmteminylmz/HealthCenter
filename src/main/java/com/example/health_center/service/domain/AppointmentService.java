package com.example.health_center.service.domain;

import com.example.health_center.entity.concretes.Appointment;
import com.example.health_center.entity.concretes.Doctor;
import com.example.health_center.entity.concretes.Examination;
import com.example.health_center.entity.concretes.Patient;
import com.example.health_center.entity.enums.AppointmentStatus;
import com.example.health_center.exception.ConflictException;
import com.example.health_center.exception.ResourceNotFoundException;
import com.example.health_center.payload.request.AppointmentRequest;
import com.example.health_center.payload.response.AppointmentResponse;
import com.example.health_center.payload.response.ResponseMessage;
import com.example.health_center.repository.AppointmentRepository;
import com.example.health_center.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public ResponseMessage<AppointmentResponse> save(String username,AppointmentRequest appointmentRequest) {

        Doctor doctor = doctorService.getByIdForCustom(appointmentRequest.getDoctorId());
        Patient patient = patientService.getByUsername(username);

        if (checkDateTimeConflictForAppointment(doctor.getId(),patient.getId(),appointmentRequest.getAppointmentDate())){
            throw new ConflictException(String.format(Messages.APPOINTMENT_TIME_CONFLICT));
        }

        Appointment appointment = createAppointment(appointmentRequest);

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return ResponseMessage.<AppointmentResponse>builder()
                .httpStatus(HttpStatus.OK)
                .message("Appointment Saved")
                .object(createAppointmentResponse(savedAppointment))
                .build();
    }

    public boolean checkDateTimeConflictForAppointment(Long doctorId, Long patientId, LocalDateTime appointmentDate) {
        LocalDateTime fiveMinutesBefore = appointmentDate.minusMinutes(5);
        LocalDateTime fiveMinutesAfter = appointmentDate.plusMinutes(5);

        boolean doctorHasConflict = appointmentRepository.existsByDoctorIdAndAppointmentDateBetween(
                doctorId, fiveMinutesBefore, fiveMinutesAfter);

        boolean patientHasConflict = appointmentRepository.existsByPatientIdAndAppointmentDateBetween(
                patientId, fiveMinutesBefore, fiveMinutesAfter);

        return doctorHasConflict || patientHasConflict;
    }


    public List<AppointmentResponse> getAll() {

        return appointmentRepository.findAll().stream().
                map(this::createAppointmentResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getById(Long id) {

        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(Messages.NOT_FOUND_APPOINTMENT_MESSAGE, id))
        );

        return createAppointmentResponse(appointment);
    }

    public Appointment getAppointmentByAppointmentId(Long appointmentId) {

        return appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(Messages.NOT_FOUND_APPOINTMENT_MESSAGE, appointmentId))
        );
    }


    public ResponseMessage<?> delete(Long id) {

        appointmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(Messages.NOT_FOUND_APPOINTMENT_MESSAGE, id))
        );

        appointmentRepository.deleteById(id);

        return ResponseMessage.builder()
                .httpStatus(HttpStatus.OK)
                .message("Deleted Successfully")
                .build();
    }

    public List<Appointment> getAppointmentsByUsernameForExamination(String username) {

        return appointmentRepository.findByDoctorUsername(username)
                .stream()
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAppointmentsByUsernameForDoctor(String username) {

        return appointmentRepository.findByDoctorUsername(username)
                .stream()
                .map(this::createAppointmentResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAppointmentsByUsernameForPatient(String username) {

        return appointmentRepository.findByPatientUsername(username)
                .stream()
                .map(this::createAppointmentResponse)
                .collect(Collectors.toList());
    }

    public void saveExaminationForAppointment(Examination examination) {

        Appointment appointment = getAppointmentByAppointmentId(examination.getAppointment().getId());
        appointment.setExamination(examination);

        appointmentRepository.save(appointment);

    }


    public Appointment createAppointment(AppointmentRequest appointmentRequest){
        return Appointment.builder()
                .appointmentDate(appointmentRequest.getAppointmentDate())
                .build();
    }

    public AppointmentResponse createAppointmentResponse(Appointment appointment){
        return AppointmentResponse.builder()
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .id(appointment.getId())
                .status(AppointmentStatus.SCHEDULED)
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getName())
                .examinationId(appointment.getExamination().getId())
                .cancellationReason(appointment.getCancellationReason())
                .build();
    }

}
