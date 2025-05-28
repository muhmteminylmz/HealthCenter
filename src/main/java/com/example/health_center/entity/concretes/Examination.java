package com.example.health_center.entity.concretes;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime examinationDate;

    private String diagnosis;

    @ManyToMany
    @JoinTable(
            name = "examination_diseases",
            joinColumns = @JoinColumn(name = "examination_id"),
            inverseJoinColumns = @JoinColumn(name = "disease_id")
    )
    private List<Disease> diseases;


    @ManyToMany
    @JoinTable(
            name = "examination_allergies",
            joinColumns = @JoinColumn(name = "examination_id"),
            inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    private List<Allergy> allergies;


    @ManyToOne
    private Patient patient;

    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY)
    private List<LabTest> labTests;

    @ManyToOne
    private Doctor doctor;


    @ManyToMany(mappedBy = "examinations", fetch = FetchType.LAZY)
    private List<Nurse> nurses;

    @OneToOne(mappedBy = "examination")
    private Appointment appointment;

    /*@PrePersist
    public void prePersist() {
        if (examinationDate == null) {
            examinationDate = LocalDateTime.now();
        }
    }*/

}
