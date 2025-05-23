package com.example.health_center.payload.request.abstracts;

import com.example.health_center.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@SuperBuilder
@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseUserRequest implements Serializable {

    @NotNull(message = "Please enter your username")
    @Size(min = 4,max = 16,message = "Your username should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your username must consist of the characters .")
    private String username;

    @NotNull(message = "Please enter your name")
    @Size(min = 2,max = 16,message = "Your name should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your name must consist of the characters .")
    private String name;

    @NotNull(message = "Please enter your surname")
    @Size(min = 2,max = 16,message = "Your surname should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your surname must consist of the characters .")
    private String surname;

    @NotNull(message = "Please enter your birthdate")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    @Past//Mutlaka gunumuzden eski bir tarih olmali
    private LocalDate birthDate;

    @NotNull(message = "Please enter your TC number")
    @Pattern(regexp = "^[1-9][0-9]{10}$", message = "Please enter a valid TC number")
    private String tc;
/*
    @NotNull
    @Size(min = 2,max = 16,message = "Your birthplace should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your birthplace must consist of the characters .")
    private String birthPlace;*/

    @NotNull(message = "Please enter your password")
    @Size(min = 8,max = 60,message = "Your password should be at least 8 chars")
    private String password;

    @NotNull(message = "Please enter your phone number")
    @Size(min = 12,max = 12,message = "Your phone number should be exact 12 chars")
    @Pattern(regexp = "^\\(?\\d{3}\\)?[-.]?\\d{3}[-.]?\\d{4}$",
            //^((\(\d{3}\))/\d{3})[-.]?\d{3}[- .]?\d{4}$
            message = "Please enter valid phone number")
    private String phoneNumber;

    @NotNull(message = "Please enter your gender")
    private Gender gender;
}