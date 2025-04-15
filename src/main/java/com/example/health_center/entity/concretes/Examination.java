package com.example.health_center.entity.concretes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime examinationDate;

    private String diagnosis;

    @ManyToOne
    private Patient patient;

    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<LabTest> labTests;

    @ManyToOne
    private Doctor doctor;


    @ManyToMany(mappedBy = "examinations", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Nurse> nurses;

    @OneToOne(mappedBy = "examination", cascade = CascadeType.REMOVE)
    private Appointment appointment;

}
