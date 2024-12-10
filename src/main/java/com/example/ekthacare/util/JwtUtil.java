package com.example.ekthacare.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.repo.UserRepository;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final UserRepository userRepository;

    // Shared secret for signing JWT (must be at least 256 bits)
    private static final String SHARED_SECRET = "thisisaverystrongsharedsecretkeywhichismorethan32bytes!"; 

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String mobile) {
        // Retrieve the user by mobile number
        User user = userRepository.findByMobile(mobile);

        // Check if user is found, otherwise throw an exception
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Generate the signing key using the shared secret (ensure it's at least 256 bits long)
        Key key = Keys.hmacShaKeyFor(SHARED_SECRET.getBytes()); 

        // Generate the JWT token
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(mobile) // Set mobile as the subject
                .setIssuedAt(new Date()) // The time the token is created
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Expiration time (1 day)
                .signWith(key, SignatureAlgorithm.HS256); // Sign with the shared key

        // Generate and return the JWT token
        String jwtToken = jwtBuilder.compact();

        // Optionally, store the JWT token in the user's record in the database
        user.setJwtToken(jwtToken); // Store the JWT token
        userRepository.save(user); // Save the updated user with the JWT token

        return jwtToken; // Return the generated JWT token
    }

    // Method to extract mobile from the JWT token
    public String extractMobile(String token) {
        // Generate the signing key using the shared secret (ensure it's at least 256 bits long)
        Key key = Keys.hmacShaKeyFor(SHARED_SECRET.getBytes());

        // Decode the JWT token and retrieve the claims
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)  // Use the key directly
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

        // Extract and return the mobile (subject) from the claims
        return claims.getSubject();
    }
}
