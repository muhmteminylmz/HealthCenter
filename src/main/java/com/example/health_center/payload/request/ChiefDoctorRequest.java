package com.example.health_center.payload.request;

import com.example.health_center.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ChiefDoctorRequest extends BaseUserRequest {

    @NotNull(message = "Duty start date is required")
    @PastOrPresent
    private LocalDate dutyStartDate;

    @NotNull(message = "Duty end date is required")
    @Future
    private LocalDate dutyEndDate;
}
