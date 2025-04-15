package com.example.health_center.entity.concretes;

import com.example.health_center.entity.abstracts.User;
import com.example.health_center.entity.enums.BloodType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
}
