package az.authservice.service;

import az.authservice.security.CustomUserDetails;

public interface JwtService {
    String generateAccessToken(CustomUserDetails userDetails);
    String extractEmail(String token);
    String extractUserId(String token);
    String extractRole(String token);
    boolean isTokenValid(String token);
}
