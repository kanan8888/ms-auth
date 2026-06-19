package az.authservice.service.impl;

import az.authservice.annotation.Log;
import az.authservice.configuration.properties.JwtProperties;
import az.authservice.dao.entity.User;
import az.authservice.dao.repository.UserRepository;
import az.authservice.dto.request.*;
import az.authservice.dto.response.AuthResponse;
import az.authservice.dto.response.ValidateResponse;
import az.authservice.enums.UserRole;
import az.authservice.exception.AuthException;
import az.authservice.messaging.AuthEventPublisher;
import az.authservice.security.CustomUserDetails;
import az.authservice.service.AuthService;
import az.authservice.service.JwtService;
import az.authservice.service.OtpService;
import az.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static az.authservice.enums.LogAction.*;
import static az.authservice.enums.UserRole.CUSTOMER;
import static az.authservice.enums.VerificationType.PHONE_VERIFICATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final OtpService otpService;
    private final AuthEventPublisher authEventPublisher;

    @Log(Register)
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AuthException.emailAlreadyInUse(request.getEmail());
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(CUSTOMER)
                .build();
        userRepository.save(user);
    }

    @Log(Login)
    public AuthResponse login(LoginRequest request) {
        try{
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var userDetails = (CustomUserDetails) authentication.getPrincipal();
            var response = buildAuthResponse(userDetails);

            log.info("ActionLog.Login.info - userId: {}", userDetails.getUserId());
            return response;
        } catch (BadCredentialsException ex) {
            throw AuthException.invalidCredentials(request.getEmail());
        }
    }

    @Log(Refresh)
    public AuthResponse refresh(RefreshTokenRequest request) {
        var refreshToken = refreshTokenService
                .findByToken(request.getRefreshToken());

        refreshTokenService.validateToken(refreshToken);

        var newRefreshToken = refreshTokenService
                .rotateRefreshToken(refreshToken);

        var userDetails = CustomUserDetails.from(refreshToken.getUser());
        var accessToken = jwtService.generateAccessToken(userDetails);

        log.info("ActionLog.Refresh.info - userId: {}", userDetails.getUserId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .userId(refreshToken.getUser().getId())
                .email(refreshToken.getUser().getEmail())
                .role(refreshToken.getUser().getRole())
                .build();
    }

    @Log(Logout)
    public void logout(RefreshTokenRequest request) {
        var refreshToken = refreshTokenService
                .findByToken(request.getRefreshToken());
        refreshTokenService.revokeToken(refreshToken);
    }

    @Log(LogoutAll)
    @Transactional
    public void logoutAll(String authHeader) {
        var token = extractTokenFromHeader(authHeader);
        var userId = UUID.fromString(jwtService.extractUserId(token));
        refreshTokenService.revokeAllByUser(userId);
        log.info("ActionLog.LogoutAll.info - userId: {}", userId);
    }

    @Log(Validate)
    public ValidateResponse validate(String authHeader) {
        String token = extractTokenFromHeader(authHeader);

        if (!jwtService.isTokenValid(token)) {
            throw AuthException.invalidToken();
        }

        var response = ValidateResponse.builder()
                .userId(UUID.fromString(jwtService.extractUserId(token)))
                .email(jwtService.extractEmail(token))
                .role(UserRole.valueOf(jwtService.extractRole(token)))
                .build();

        log.info("ActionLog.Validate.info - userId: {}", response.getUserId());
        return response;
    }

    @Log(SendOtp)
    public void sendOtp(UUID userId, SendOtpRequest request) {
        if (userRepository.existsByPhoneAndPhoneVerified(request.getPhoneNumber(), true)) {
            throw AuthException.phoneAlreadyExists(request.getPhoneNumber());
        }

        var otp = otpService.generateAndSaveOtp(request.getPhoneNumber());
        authEventPublisher.publishVerification(userId, request.getPhoneNumber(), PHONE_VERIFICATION, otp);
    }

    @Log(VerifyOtp)
    public void verifyOtp(UUID userId, VerifyOtpRequest request) {
        otpService.verifyOtp(request.getPhoneNumber(), request.getOtpCode());

        var user = userRepository.findById(userId)
                .orElseThrow(() -> AuthException.userNotFound(userId));

        user.setPhone(request.getPhoneNumber());
        user.setPhoneVerified(true);
        userRepository.save(user);
    }

    @Log(AdminRegister)
    public void adminRegister(AdminRegisterRequest request) {   //random password generation?//
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AuthException.emailAlreadyInUse(request.getEmail());
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isPhoneVerified(false)
                .role(request.getRole())
                .build();

        userRepository.save(user);
    }


    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(jwtProperties.getTokenPrefix() + " ")) {
            throw AuthException.invalidToken();
        }
        return authHeader.substring(jwtProperties.getTokenPrefix().length());
    }

    private AuthResponse buildAuthResponse(CustomUserDetails userDetails) {
        var accessToken = jwtService.generateAccessToken(userDetails);
        var refreshToken = refreshTokenService
                .createRefreshToken(userDetails.getUserId()).getToken();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userDetails.getUserId())
                .email(userDetails.getUsername())
                .role(userDetails.getRole())
                .build();
    }
}
