package com.example.health_center.payload.response;

import com.example.health_center.payload.request.abstracts.BaseUserRequest;
import com.example.health_center.payload.response.abstracts.BaseUserResponse;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ChiefDoctorResponse extends BaseUserResponse {

    private LocalDate dutyStartDate;
    private LocalDate dutyEndDate;
}
