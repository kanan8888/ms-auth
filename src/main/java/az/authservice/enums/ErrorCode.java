package az.authservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    AUTH_EMAIL_ALREADY_IN_USE(HttpStatus.CONFLICT, "auth.email.already.in.use"),
    AUTH_PHONE_ALREADY_IN_USE(HttpStatus.CONFLICT, "auth.phone.already.in.use"),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "auth.user.not.found"),
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "auth.invalid.credentials"),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "auth.invalid.token"),
    AUTH_REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "auth.refresh.token.not.found"),
    AUTH_REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "auth.refresh.token.revoked"),
    AUTH_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "auth.refresh.token.expired"),

    OTP_NOT_FOUND(HttpStatus.BAD_REQUEST, "auth.otp.not.found"),
    OTP_INVALID(HttpStatus.BAD_REQUEST, "auth.otp.invalid"),
    OTP_TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "auth.otp.too.many.attempts"),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "general.validation.error"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "general.internal.server.error"),
    INVALID_REQUEST_FORMAT(HttpStatus.BAD_REQUEST, "general.invalid.request.format");

    private final HttpStatus status;
    private final String messageKey;
}
