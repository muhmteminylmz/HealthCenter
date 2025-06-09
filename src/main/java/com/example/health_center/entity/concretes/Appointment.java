package com.example.health_center.entity.concretes;

import com.example.health_center.entity.enums.AppointmentStatus;
import com.example.health_center.entity.enums.CancellationReason;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appointmentDate;

    private AppointmentStatus status;

    private CancellationReason cancellationReason;

    @ManyToOne
    @JsonIgnore
    private Patient patient;

    @ManyToOne
    @JsonIgnore
    private Doctor doctor;

    @OneToOne(mappedBy = "appointment")
    private Examination examination;
}
