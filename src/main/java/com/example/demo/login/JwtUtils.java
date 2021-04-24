package com.example.demo.login;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.demo.login.User;

@Component
public class JwtUtils {

    @Value("WatSecretKey")
    private String jwtSecret;

    public String generateJwtToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
