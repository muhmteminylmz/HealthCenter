package com.example.health_center.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
info = @Info(title = "HealthCenter API",version = "1.0.0"),
security = @SecurityRequirement(name = "Bearer"))
@SecurityScheme(name = "Bearer",type = SecuritySchemeType.HTTP,scheme = "Bearer")

public class OpenAPIConfig {
//localhost:8080/swagger-ui/index.html

}