package az.authservice.service.impl;

import az.authservice.exception.AuthException;
import az.authservice.service.OtpService;
import az.authservice.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String OTP_ATTEMPTS_PREFIX = "otp:attempts:";
    private static final long OTP_TTL_SECONDS = 120;
    private static final long ATTEMPTS_TTL_SECONDS = 600;
    private static final int MAX_ATTEMPTS = 3;

    private final RedisService redisService;


    public String generateAndSaveOtp(String phoneNumber) {
        var otp = generateOtp();
        redisService.setValue(OTP_KEY_PREFIX + phoneNumber, otp, OTP_TTL_SECONDS);
        log.info("ActionLog.SendOtp.success - phone: {}", phoneNumber);
        return otp;
    }

    public void verifyOtp(String phoneNumber, String otpCode) {
        checkAttempts(phoneNumber);

        var otp = redisService.getValue(OTP_KEY_PREFIX + phoneNumber)
                .orElseThrow(() -> AuthException.otpNotFound(phoneNumber));

        redisService.increment(OTP_ATTEMPTS_PREFIX + phoneNumber, ATTEMPTS_TTL_SECONDS);

        if (!otp.equals(otpCode)) {
            log.warn("ActionLog.VerifyOtp.failed - phone: {}", phoneNumber);
            throw AuthException.otpInvalid(phoneNumber);
        }

        clearOtp(phoneNumber);
        log.info("ActionLog.VerifyOtp.success - phone: {}", phoneNumber);
    }


    private void checkAttempts(String phoneNumber) {
        int attempts = redisService.getCount(OTP_ATTEMPTS_PREFIX + phoneNumber);
        if (attempts >= MAX_ATTEMPTS) {
            log.warn("ActionLog.VerifyOtp.failed - phone: {}, reason: too many attempts", phoneNumber);
            throw AuthException.otpTooManyAttempts(phoneNumber);
        }
    }

    private void clearOtp(String phoneNumber) {
        redisService.delete(OTP_KEY_PREFIX + phoneNumber);
        redisService.delete(OTP_ATTEMPTS_PREFIX + phoneNumber);
    }

    private String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1000000));
    }
}
