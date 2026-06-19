package az.authservice.dto.response;

import az.authservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ValidateResponse {
    private UUID userId;
    private String email;
    private UserRole role;
}
