package com.example.health_center.payload.request;

import com.example.health_center.entity.enums.TestStatus;
import com.example.health_center.entity.enums.TestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LabTestRequest {

    @NotNull(message = "Test type is required")
    private TestType testType;

    @NotNull(message = "Test date is required")
    private LocalDateTime testDate;

    private String testResult;

    private TestStatus status;

    @NotNull(message = "Price is required")
    private Integer price;

    @NotNull(message = "Examination id is required")
    private Long examinationId;

}
