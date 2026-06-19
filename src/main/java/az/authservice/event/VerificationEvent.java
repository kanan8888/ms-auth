package az.authservice.event;

import az.authservice.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationEvent {
    private UUID userId;
    private String recipient;
    private String otpCode;
    private VerificationType type;
    private String timestamp;
}
