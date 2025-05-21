package com.example.health_center.controller;

import com.example.health_center.entity.enums.RoleType;
import com.example.health_center.payload.request.LoginRequest;
import com.example.health_center.payload.response.AuthResponse;
import com.example.health_center.security.jwt.JwtUtils;
import com.example.health_center.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    public final JwtUtils jwtUtils;
    public final AuthenticationManager authenticationManager;

    @PostMapping("/login") // http://localhost:8080/auth/login
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(username,password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.
                getAuthorities().
                stream().
                map(GrantedAuthority::getAuthority).
                        toList();

        Optional<String> role = roles.stream().findFirst();

        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.username(username);
        authResponse.token(token);
        authResponse.name(userDetails.getName());

        if(role.isPresent()){
            authResponse.role(role.get());
            if (role.get().equalsIgnoreCase(RoleType.DOCTOR.name())){
                authResponse.isFamilyDoctor(userDetails.getIsFamilyDoctor().toString());
            }
        }

        return ResponseEntity.ok(authResponse.build());
    }

}