package az.authservice.exception.handler;

import az.authservice.dto.response.ErrorResponse;
import az.authservice.enums.ErrorCode;
import az.authservice.exception.base.AppException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException ex,
            HttpServletRequest request) {

        log.error("ActionLog.App.error - errorCode: {}, path: {}, reason: {}",
                ex.getErrorCode().name(),
                request.getRequestURI(),
                ex.getMessage());

        String userMessage = messageSource.getMessage(
                ex.getErrorCode().getMessageKey(),
                null,
                LocaleContextHolder.getLocale()
        );

        return buildResponse(ex.getErrorCode(), userMessage, request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("ActionLog.Validation.error - path: {}, reason: {}",
                request.getRequestURI(), message);

        return buildResponse(ErrorCode.VALIDATION_ERROR, message, request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("ActionLog.Format.error - path: {}, reason: {}",
                request.getRequestURI(), ex.getMessage());

        return buildResponse(
                ErrorCode.INVALID_REQUEST_FORMAT,
                resolveMessage(ErrorCode.INVALID_REQUEST_FORMAT.getMessageKey()),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("ActionLog.Format.error - path: {}, parameter: {}, reason: {}",
                request.getRequestURI(), ex.getName(), ex.getMessage());

        return buildResponse(
                ErrorCode.INVALID_REQUEST_FORMAT,
                resolveMessage(ErrorCode.INVALID_REQUEST_FORMAT.getMessageKey()),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(
            ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request) {

        log.error("ActionLog.OptimisticLock.error - path: {}, reason: {}",
                request.getRequestURI(), ex.getMessage());

        return buildResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                resolveMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessageKey()),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        log.error("ActionLog.Unexpected.error - path: {}, reason: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        return buildResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                resolveMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessageKey()),
                request.getRequestURI()
        );
    }


    private ResponseEntity<ErrorResponse> buildResponse(
            ErrorCode errorCode, String message, String path) {
        return ResponseEntity.status(errorCode.getStatus()).body(
                ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .errorCode(errorCode.name())
                        .message(message)
                        .timestamp(Instant.now())
                        .path(path)
                        .build()
        );
    }

    private String resolveMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
