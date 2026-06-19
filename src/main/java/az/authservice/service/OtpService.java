package az.authservice.service;

public interface OtpService {
    String generateAndSaveOtp(String phoneNumber);
    void verifyOtp(String phoneNumber, String otpCode);
}
