package com.example.health_center.entity.concretes;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicine;

    private String dosage;

    private String prescriptionNote;

    private LocalDate followUpDate;

    @ManyToOne
    private Doctor doctor;

    @ManyToOne
    private Patient patient;


}
