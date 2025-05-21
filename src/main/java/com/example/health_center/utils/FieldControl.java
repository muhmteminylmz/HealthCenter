package com.example.health_center.utils;

import com.example.health_center.exception.ConflictException;
import com.example.health_center.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FieldControl {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final ChiefDoctorRepository chiefDoctorRepository;
    private final NurseRepository nurseRepository;
    private final PatientRepository patientRepository;

    public void checkDuplicate(String... values) {
        String username = values[0];
        String tc = values[1];
        String phone = values[2];

        if (adminRepository.existsByUsername(username) ||
                doctorRepository.existsByUsername(username) ||
                chiefDoctorRepository.existsByUsername(username) ||
                nurseRepository.existsByUsername(username) ||
                patientRepository.existsByUsername(username) /*||
                guestUserRepository.existsByUsername(username)*/) {

            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_USERNAME, username));

        } else if (adminRepository.existsByTc(tc) ||
                doctorRepository.existsByTc(tc) ||
                chiefDoctorRepository.existsByTc(tc) ||
                nurseRepository.existsByTc(tc) ||
                patientRepository.existsByTc(tc) /*||
                guestUserRepository.existsBySsn(ssn)*/) {

            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_TC, tc));

        } else if (adminRepository.existsByPhoneNumber(phone) ||
                doctorRepository.existsByPhoneNumber(phone) ||
                chiefDoctorRepository.existsByPhoneNumber(phone) ||
                nurseRepository.existsByPhoneNumber(phone) ||
                patientRepository.existsByPhoneNumber(phone) /*||
                guestUserRepository.existsByPhoneNumber(phone)*/) {

            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_MESSAGE_PHONE_NUMBER, phone));

        }
    }
}
