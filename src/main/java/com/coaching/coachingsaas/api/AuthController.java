package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.*;
import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.domain.EmailOtp;
import com.coaching.coachingsaas.domain.OtpPurpose;
import com.coaching.coachingsaas.repo.CoachingRepository;
import com.coaching.coachingsaas.security.JwtService;
import com.coaching.coachingsaas.service.CoachingAuthService;
import com.coaching.coachingsaas.service.EmailOtpService;
import com.coaching.coachingsaas.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    private final CoachingRepository coachingRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final CoachingAuthService coachingAuthService;
    private final EmailOtpService emailOtpService;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public AuthController(CoachingRepository coachingRepository,
                          PasswordService passwordService,
                          JwtService jwtService,
                          CoachingAuthService coachingAuthService,
                          EmailOtpService emailOtpService) {

        this.coachingRepository = coachingRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.coachingAuthService = coachingAuthService;
        this.emailOtpService = emailOtpService;
    }

    // ----------------------------
    // Coaching Auth APIs
    // ----------------------------

    @GetMapping("/api/auth/exists")
    public ResponseEntity<?> exists(@RequestParam String email) {
        boolean ok = coachingRepository.existsByEmail(email.trim().toLowerCase());
        return ResponseEntity.ok(Map.of("exists", ok));
    }

    @PostMapping("/api/auth/register/send-otp")
    public ResponseEntity<?> registerSendOtp(@Valid @RequestBody RegisterSendOtpRequest req) {
        String email = req.email.trim().toLowerCase();

        if (coachingRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email already registered. Please login.");
        }

        emailOtpService.sendOtpForRegister(
                email,
                req.coachingName.trim(),
                req.ownerName.trim(),
                req.phone.trim()
        );

        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/api/auth/register/verify-otp")
    public ResponseEntity<?> registerVerifyOtp(@Valid @RequestBody EmailOtpVerifyRequest req) {
        String email = req.email.trim().toLowerCase();

        EmailOtp otp = emailOtpService.verifyOtpOrThrow(email, req.otp, OtpPurpose.REGISTER);

        Coaching c = coachingAuthService.createIfNotExists(
                otp.getCoachingName(),
                otp.getOwnerName(),
                otp.getPhone(),
                email
        );

        String token = jwtService.generateCoachingToken(c.getId());
        return ResponseEntity.ok(new AuthResponse(c.getId(), token, c.getPlanType().name()));
    }

    @PostMapping("/api/auth/login/send-otp")
    public ResponseEntity<?> loginSendOtp(@Valid @RequestBody LoginSendOtpRequest req) {
        String email = req.email.trim().toLowerCase();

        if (!coachingRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email not registered. Please register first.");
        }

        emailOtpService.sendOtpForLogin(email);
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/api/auth/login/verify-otp")
    public ResponseEntity<?> loginVerifyOtp(@Valid @RequestBody EmailOtpVerifyRequest req) {
        String email = req.email.trim().toLowerCase();

        emailOtpService.verifyOtpOrThrow(email, req.otp, OtpPurpose.LOGIN);

        Coaching c = coachingAuthService.getOrThrowByEmail(email);

        String token = jwtService.generateCoachingToken(c.getId());
        return ResponseEntity.ok(new AuthResponse(c.getId(), token, c.getPlanType().name()));
    }

    @PostMapping("/api/auth/login-password")
    public ResponseEntity<?> loginPassword(@Valid @RequestBody LoginRequest req) {
        Coaching c = coachingRepository.findByEmail(req.email.trim().toLowerCase()).orElse(null);

        if (c == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        if (!passwordService.matches(req.password, c.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtService.generateCoachingToken(c.getId());
        return ResponseEntity.ok(new AuthResponse(c.getId(), token, c.getPlanType().name()));
    }

    // ----------------------------
    // Admin Login API
    // ----------------------------

    @PostMapping("/api/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody AdminLoginRequest req) {
        String email = req.email.trim().toLowerCase();
        String password = req.password.trim();

        if (!adminEmail.trim().toLowerCase().equals(email) || !adminPassword.equals(password)) {
            return ResponseEntity.status(401).body("Invalid admin credentials");
        }

        String token = jwtService.generateAdminToken();
        return ResponseEntity.ok(new AdminAuthResponse(token, "ADMIN"));
    }
}