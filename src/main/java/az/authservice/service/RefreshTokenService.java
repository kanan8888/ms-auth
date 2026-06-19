package az.authservice.service;

import az.authservice.dao.entity.RefreshToken;

import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(UUID userId);
    RefreshToken rotateRefreshToken(RefreshToken oldToken);
    void validateToken(RefreshToken token);
    void revokeToken(RefreshToken token);
    void revokeAllByUser(UUID userId);
    RefreshToken findByToken(String token);
    void cleanupExpiredTokens();
}
