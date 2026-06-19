package az.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String errorCode;
    private String message;
    private Instant timestamp;
    private String path;
}
