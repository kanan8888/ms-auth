package az.authservice.dto.request;

import az.authservice.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class AdminRegisterRequest {

    @NotBlank(message = "{validation.email.blank}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.password.blank}")
    @Size(min = 8, message = "{validation.password.size}")
    @ToString.Exclude
    private String password;

    @NotNull(message = "{validation.role.null}")
    private UserRole role;
}
