package az.authservice.exception;

import az.authservice.enums.ErrorCode;
import az.authservice.exception.base.AppException;

import java.util.UUID;

public class AuthException extends AppException {

    public static AuthException emailAlreadyInUse(String email) {
        return new AuthException(
                ErrorCode.AUTH_EMAIL_ALREADY_IN_USE,
                "Registration attempt with existing email: " + email
        );
    }

    public static AuthException phoneAlreadyExists(String phone) {
        return new AuthException(ErrorCode.AUTH_PHONE_ALREADY_IN_USE,
                "Phone already in use: " + phone);
    }

    public static AuthException userNotFound(UUID userId) {
        return new AuthException(
                ErrorCode.AUTH_USER_NOT_FOUND,
                "User not found with id: " + userId
        );
    }

    public static AuthException invalidCredentials(String email) {
        return new AuthException(
                ErrorCode.AUTH_INVALID_CREDENTIALS,
                "Failed login attempt for email: " + email
        );
    }

    public static AuthException invalidToken() {
        return new AuthException(
                ErrorCode.AUTH_INVALID_TOKEN,
                "Invalid or missing token"
        );
    }

    public static AuthException refreshTokenNotFound() {
        return new AuthException(
                ErrorCode.AUTH_REFRESH_TOKEN_NOT_FOUND,
                "Refresh token not found in database"
        );
    }

    public static AuthException refreshTokenRevoked(UUID userId) {
        return new AuthException(
                ErrorCode.AUTH_REFRESH_TOKEN_REVOKED,
                "Revoked refresh token reuse detected for userId: " + userId
        );
    }

    public static AuthException refreshTokenExpired() {
        return new AuthException(
                ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED,
                "Expired refresh token used"
        );
    }

    public static AuthException otpNotFound(String phone) {
        return new AuthException(ErrorCode.OTP_NOT_FOUND,
                "OTP not found or expired for phone: " + phone);
    }

    public static AuthException otpInvalid(String phone) {
        return new AuthException(ErrorCode.OTP_INVALID,
                "Invalid OTP for phone: " + phone);
    }

    public static AuthException otpTooManyAttempts(String phone) {
        return new AuthException(ErrorCode.OTP_TOO_MANY_ATTEMPTS,
                "Too many OTP attempts for phone: " + phone);
    }

    public AuthException(ErrorCode errorCode, String logMessage) {
        super(errorCode, logMessage);
    }
}
