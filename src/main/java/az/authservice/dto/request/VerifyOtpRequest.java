package az.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class VerifyOtpRequest {

    @NotBlank(message = "{validation.phone.blank}")
    private String phoneNumber;

    @NotBlank(message = "{validation.otp.blank}")
    @Size(min = 6, max = 6, message = "{validation.otp.size}")
    private String otpCode;
}
