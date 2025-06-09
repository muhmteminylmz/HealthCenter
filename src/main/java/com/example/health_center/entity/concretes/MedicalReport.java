package com.example.health_center.entity.concretes;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MedicalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private LocalDateTime reportDate;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne
    private Disease diagnosis;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    @PrePersist
    public void prePersist() {
        if (reportDate == null) {
            reportDate = LocalDateTime.now();
        }
    }
}
