package az.authservice.service;

import az.authservice.dto.request.*;
import az.authservice.dto.response.AuthResponse;
import az.authservice.dto.response.ValidateResponse;

import java.util.UUID;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);
    void logoutAll(String authHeader);
    ValidateResponse validate(String authHeader);
    void sendOtp(UUID userId, SendOtpRequest request);
    void verifyOtp(UUID userId, VerifyOtpRequest request);
    void adminRegister(AdminRegisterRequest request);
}
