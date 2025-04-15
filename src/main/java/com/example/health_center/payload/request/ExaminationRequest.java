package com.example.health_center.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ExaminationRequest {

    @NotNull(message = "Examination date is required")
    @Future(message = "Examination date must be in the future")
    private LocalDateTime examinationDate;

    private String diagnosis;

    private Long appointmentId;
}
