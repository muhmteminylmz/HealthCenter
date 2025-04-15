package com.example.health_center.entity.concretes;

import com.example.health_center.entity.abstracts.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChiefDoctor extends User {

    private LocalDate dutyStartDate;

    private LocalDate dutyEndDate;

}
