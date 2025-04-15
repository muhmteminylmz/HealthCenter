package com.example.health_center.entity.concretes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FamilyDoctor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //User dan extend edilmedi
    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRole userRole;

    @OneToOne
    private Doctor doctor;

    @OneToMany(mappedBy = "familyDoctor", cascade = CascadeType.REMOVE)
    private List<Patient> patients;
}
