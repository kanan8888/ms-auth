package az.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class SendOtpRequest {

    @NotBlank(message = "{validation.phone.blank}")
    @Pattern(regexp = "^\\+994[0-9]{9}$", message = "{validation.phone.invalid}")
    private String phoneNumber;
}
