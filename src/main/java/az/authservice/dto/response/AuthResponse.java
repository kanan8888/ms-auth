package az.authservice.dto.response;

import az.authservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class AuthResponse {

    @ToString.Exclude
    private String accessToken;

    @ToString.Exclude
    private String refreshToken;

    private UUID userId;
    private String email;
    private UserRole role;
}
