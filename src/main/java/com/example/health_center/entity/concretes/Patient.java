package com.example.health_center.entity.concretes;

import com.example.health_center.entity.abstracts.User;
import com.example.health_center.entity.enums.BloodType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Patient extends User {

    private BloodType bloodType;

    @ManyToOne
    @JsonIgnore
    private FamilyDoctor familyDoctor;

    @ManyToMany
    @JoinTable(
            name = "patient_allergies",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    private List<Allergy> allergies;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<LabTest> labTests;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Examination> examination;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Prescription> prescriptions;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MedicalReport> medicalReports;
}
