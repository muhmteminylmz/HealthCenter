package com.example.health_center.entity.concretes;

import com.example.health_center.entity.enums.AppointmentStatus;
import com.example.health_center.entity.enums.CancellationReason;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToOne
    private Examination examination;
}
