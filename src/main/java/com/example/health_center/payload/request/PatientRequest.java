package com.example.health_center.payload.request;

import com.example.health_center.entity.enums.BloodType;
import com.example.health_center.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PatientRequest extends BaseUserRequest {

    @NotNull(message = "Blood type is required")
    private BloodType bloodType;
}
