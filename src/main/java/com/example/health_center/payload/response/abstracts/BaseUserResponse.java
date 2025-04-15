package com.example.health_center.payload.response.abstracts;

import com.example.health_center.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BaseUserResponse {

    private Long userId;
    private String userName;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String tc;
    private String phoneNumber;
    private Gender gender;
}
