package az.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "{validation.email.blank}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @ToString.Exclude
    @NotBlank(message = "{validation.password.blank}")
    @Size(min = 8, message = "{validation.password.size}")
    private String password;
}
