package az.authservice.exception.base;

import az.authservice.enums.ErrorCode;
import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode, String logMessage) {
        super(logMessage);
        this.errorCode = errorCode;
    }
}
