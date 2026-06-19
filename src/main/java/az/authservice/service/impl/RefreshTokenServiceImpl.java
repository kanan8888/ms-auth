package az.authservice.service.impl;

import az.authservice.annotation.Log;
import az.authservice.configuration.properties.JwtProperties;
import az.authservice.dao.entity.RefreshToken;
import az.authservice.dao.repository.RefreshTokenRepository;
import az.authservice.dao.repository.UserRepository;
import az.authservice.exception.AuthException;
import az.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

import static az.authservice.enums.LogAction.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Log(TokenCreate)
    public RefreshToken createRefreshToken(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.userNotFound(userId));

        var refreshToken = RefreshToken.builder()
                .user(user)
                .token(generateToken())
                .expiresAt(Instant.now()
                        .plusMillis(jwtProperties.getRefreshTokenExpiration()))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Log(TokenRotate)
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        revokeToken(oldToken);
        return createRefreshToken(oldToken.getUser().getId());
    }

    @Log(TokenValidate)
    @Transactional(noRollbackFor = AuthException.class)
    public void validateToken(RefreshToken token) {
        var userId = token.getUser().getId();

        if (token.isRevoked()) {  // Reuse Detection
            refreshTokenRepository.revokeAllByUserId(userId); // !
            throw AuthException.refreshTokenRevoked(userId);
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw AuthException.refreshTokenExpired();
        }
    }

    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void revokeAllByUser(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(AuthException::refreshTokenNotFound);
    }

    @Log(Cleanup)
    @Transactional
    public void cleanupExpiredTokens() {
        var threshold = Instant.now()
                .minus(jwtProperties.getCleanupThresholdDays(), ChronoUnit.DAYS);
        int deleted = refreshTokenRepository.deleteExpiredTokens(threshold);
        log.info("ActionLog.Cleanup.info - deletedCount: {}", deleted);
    }

    private String generateToken() {
        var bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
