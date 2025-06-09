package com.example.health_center.security.jwt;

import com.example.health_center.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${backendapi.app.jwtSecret}")
    private String jwtSecret;

    @Value("${backendapi.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        //EXTRA
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return generateTokenFromUsername(userPrincipal.getUsername(),roles);
    }

    private String generateTokenFromUsername(String username, List<String> roles) {

        return Jwts.
                builder().
                setSubject(username).
                claim("roles", roles).
                setIssuedAt(new Date()).
                setExpiration(new Date((new Date().getTime() + jwtExpirationMs))).
                signWith(SignatureAlgorithm.HS512, jwtSecret).
                compact();

    }

    public boolean validateJwtToken(String authToken){

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Jwt token is expired : {}",e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT TOKEN is unsupported : {}",e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid Jwt Token : {}",e.getMessage());
        } catch (SignatureException e) {
            logger.error("Invalid Jwt signature : {}",e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Jwt claims string is empty : {}",e.getMessage());
        }
        return false;
    }


    public String getUserNameFromJwtToken(String token){

        return Jwts.parser().
                setSigningKey(jwtSecret).
                parseClaimsJws(token).
                getBody().
                getSubject();
    }


}