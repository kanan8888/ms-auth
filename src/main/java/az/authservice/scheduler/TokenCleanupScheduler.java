package az.authservice.scheduler;

import az.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredTokens() {
        refreshTokenService.cleanupExpiredTokens();
    }
}
