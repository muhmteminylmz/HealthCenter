package com.example.health_center.security.service;

import com.example.health_center.entity.concretes.*;
import com.example.health_center.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ChiefDoctorRepository chiefDoctorRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final PatientRepository patientRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //User i UserDetails e cevirecegiz

        ChiefDoctor chiefDoctor = chiefDoctorRepository.findByUsernameEquals(username);
        Doctor doctor = doctorRepository.findByUsernameEquals(username);
        Nurse nurse = nurseRepository.findByUsernameEquals(username);
        Patient patient = patientRepository.findByUsernameEquals(username);
        Admin admin = adminRepository.findByUsernameEquals(username);

        if(chiefDoctor != null){
            return new UserDetailsImpl(
                    chiefDoctor.getId(),
                    chiefDoctor.getUsername(),
                    chiefDoctor.getName(),
                    false,
                    chiefDoctor.getPassword(),
                    chiefDoctor.getUserRole().getRoleType().name());
        } else if (doctor != null) {
            return new UserDetailsImpl(
                    doctor.getId(),
                    doctor.getUsername(),
                    doctor.getName(),
                    doctor.isFamilyDoctor(),
                    doctor.getPassword(),
                    doctor.getUserRole().getRoleType().name()
            );
        } else if (nurse != null) {
            return new UserDetailsImpl(
                    nurse.getId(),
                    nurse.getUsername(),
                    nurse.getName(),
                    false,
                    nurse.getPassword(),
                    nurse.getUserRole().getRoleType().name()
            );
        }else if (patient != null) {
            return new UserDetailsImpl(
                    patient.getId(),
                    patient.getUsername(),
                    patient.getName(),
                    false,
                    patient.getPassword(),
                    patient.getUserRole().getRoleType().name()
            );
        }else if (admin != null) {
            return new UserDetailsImpl(
                    admin.getId(),
                    admin.getUsername(),
                    admin.getName(),
                    false,
                    admin.getPassword(),
                    admin.getUserRole().getRoleType().name()
            );
        }
        throw new UsernameNotFoundException("User"+ username +" not found");
    }
}
