package com.example.health_center.payload.response;

import com.example.health_center.entity.concretes.Allergy;
import com.example.health_center.entity.concretes.Disease;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ExaminationResponse {

    private Long id;
    private LocalDateTime examinationDate;
    private List<AllergyResponse> allergies;
    private List<DiseaseResponse> diseases;
    private String diagnosis;

    private PatientResponse patient;
    private DoctorResponse doctors;
    private List<NurseResponse> nurses;
    private AppointmentResponse appointment;
}
