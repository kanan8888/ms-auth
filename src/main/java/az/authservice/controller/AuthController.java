package az.authservice.controller;

import az.authservice.dto.request.*;
import az.authservice.dto.response.AuthResponse;
import az.authservice.dto.response.ValidateResponse;
import az.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
    }

    @PostMapping("/logout-all")
    @ResponseStatus(NO_CONTENT)
    public void logoutAll(@RequestHeader("Authorization") String authHeader) {
        authService.logoutAll(authHeader);
    }

    @GetMapping("/validate")
    public ValidateResponse validate(@RequestHeader("Authorization") String authHeader) {
        return authService.validate(authHeader);
    }

    @PostMapping("/otp/send")
    public void sendOtp(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody SendOtpRequest request) {
        authService.sendOtp(userId, request);
    }

    @PostMapping("/otp/verify")
    public void verifyOtp(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(userId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    @ResponseStatus(CREATED)
    public void adminRegister(@Valid @RequestBody AdminRegisterRequest request) {
        authService.adminRegister(request);
    }
}
