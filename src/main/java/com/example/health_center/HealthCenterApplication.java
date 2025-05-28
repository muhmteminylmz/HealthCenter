package com.example.health_center;

import com.example.health_center.entity.enums.Gender;
import com.example.health_center.entity.enums.RoleType;
import com.example.health_center.payload.request.AdminRequest;
import com.example.health_center.service.domain.AdminService;
import com.example.health_center.service.domain.UserRoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class HealthCenterApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final AdminService adminService;

	public HealthCenterApplication(UserRoleService userRoleService, AdminService adminService) {
		this.userRoleService = userRoleService;
		this.adminService = adminService;
	}

	public static void main(String[] args) {
		SpringApplication.run(HealthCenterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		if (userRoleService.getAllUserRole().isEmpty()){
			userRoleService.save(RoleType.ADMIN);
			userRoleService.save(RoleType.PATIENT);
			userRoleService.save(RoleType.DOCTOR);
			userRoleService.save(RoleType.NURSE);
			userRoleService.save(RoleType.CHIEF_DOCTOR);
			userRoleService.save(RoleType.FAMILY_DOCTOR);
			userRoleService.save(RoleType.GUESTUSER);
		}


		if (adminService.countAllAdmin() == 0){
			AdminRequest admin = new AdminRequest();
			admin.setUsername("Admin");
			admin.setTc("11111111111");
			admin.setPassword("12345678");
			admin.setName("Admin");
			admin.setSurname("Admin");
			admin.setPhoneNumber("555-444-4331");
			admin.setGender(Gender.FEMALE);
			admin.setBirthDate(LocalDate.of(2002,5,2));
			adminService.save(admin);
		}
	}
}
