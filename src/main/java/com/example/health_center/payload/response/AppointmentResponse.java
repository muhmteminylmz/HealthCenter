package com.example.health_center.payload.response;

import com.example.health_center.entity.enums.AppointmentStatus;
import com.example.health_center.entity.enums.CancellationReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AppointmentResponse {

    private Long id;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private CancellationReason cancellationReason;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private Long examinationId;
}
