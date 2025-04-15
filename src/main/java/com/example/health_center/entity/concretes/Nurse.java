package com.example.health_center.entity.concretes;

import com.example.health_center.entity.abstracts.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Nurse extends User {

    private LocalDate dutyStartDate;

    private LocalDate dutyEndDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @ManyToMany
    @JoinTable(
            name = "nurse_examination_table",
            joinColumns = @JoinColumn(name = "nurse_id"),
            inverseJoinColumns = @JoinColumn(name = "examination_id")
    )
    private List<Examination> examinations;

}
