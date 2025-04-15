package com.example.health_center.entity.concretes;

import com.example.health_center.entity.enums.TestStatus;
import com.example.health_center.entity.enums.TestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.print.Doc;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TestType testType;

    private LocalDateTime testDate;

    private String testResult;

    private TestStatus status;

    private Integer price;

    @ManyToOne
    private Examination examination;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;


}
