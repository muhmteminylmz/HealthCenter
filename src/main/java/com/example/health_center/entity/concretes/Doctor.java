package com.example.health_center.entity.concretes;

import com.example.health_center.entity.abstracts.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Doctor extends User {

    @Column(name = "isFamilyDoctor")
    private boolean isFamilyDoctor;

    private LocalDate dutyStartDate;

    private LocalDate dutyEndDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.REMOVE)
    private List<Appointment> appointment;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.REMOVE)
    private List<LabTest> labTest;

    @OneToOne(mappedBy = "doctor", cascade = CascadeType.PERSIST,orphanRemoval = true)
    private FamilyDoctor familyDoctor;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.REMOVE)
    private List<Examination> examination;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.REMOVE)
    private List<Prescription> prescriptions;
}
