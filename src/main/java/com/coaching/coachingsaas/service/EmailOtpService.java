package com.coaching.coachingsaas.service;

import com.coaching.coachingsaas.domain.EmailOtp;
import com.coaching.coachingsaas.domain.OtpPurpose;
import com.coaching.coachingsaas.repo.EmailOtpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class EmailOtpService {

    private final EmailOtpRepository emailOtpRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordService passwordService;

    private final int expiryMinutes;
    private final int cooldownSeconds;

    private final SecureRandom random = new SecureRandom();

    public EmailOtpService(EmailOtpRepository emailOtpRepository,
                           EmailSenderService emailSenderService,
                           PasswordService passwordService,
                           @Value("${app.otp.expiry-minutes:5}") int expiryMinutes,
                           @Value("${app.otp.cooldown-seconds:30}") int cooldownSeconds) {
        this.emailOtpRepository = emailOtpRepository;
        this.emailSenderService = emailSenderService;
        this.passwordService = passwordService;
        this.expiryMinutes = expiryMinutes;
        this.cooldownSeconds = cooldownSeconds;
    }

    public void sendOtpForRegister(String email, String coachingName, String ownerName, String phone) {
        sendOtp(email, OtpPurpose.REGISTER, coachingName, ownerName, phone);
    }

    public void sendOtpForLogin(String email) {
        sendOtp(email, OtpPurpose.LOGIN, null, null, null);
    }

    private void sendOtp(String email, OtpPurpose purpose, String coachingName, String ownerName, String phone) {
        emailOtpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose).ifPresent(last -> {
            Duration diff = Duration.between(last.getCreatedAt(), LocalDateTime.now());
            if (diff.getSeconds() < cooldownSeconds) {
                throw new IllegalStateException(
                        "Please wait " + (cooldownSeconds - diff.getSeconds()) + " seconds before requesting OTP again."
                );
            }
        });

        String otp = generate6DigitOtp();
        String otpHash = passwordService.hash(otp);

        EmailOtp entity = new EmailOtp();
        entity.setEmail(email);
        entity.setPurpose(purpose);
        entity.setOtpHash(otpHash);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        entity.setUsed(false);

        // save register details
        entity.setCoachingName(coachingName);
        entity.setOwnerName(ownerName);
        entity.setPhone(phone);

        emailOtpRepository.save(entity);

        System.out.println("✅ OTP saved: email=" + email + " purpose=" + purpose + " expiresAt=" + entity.getExpiresAt());

        // send email using SendGrid service
        emailSenderService.sendOtpEmail(email, otp);
    }

    public EmailOtp verifyOtpOrThrow(String email, String otp, OtpPurpose purpose) {
        EmailOtp last = emailOtpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new IllegalStateException("OTP not found. Please request a new OTP."));

        if (last.isUsed()) {
            throw new IllegalStateException("OTP already used. Please request a new OTP.");
        }

        if (LocalDateTime.now().isAfter(last.getExpiresAt())) {
            throw new IllegalStateException("OTP expired. Please request a new OTP.");
        }

        if (!passwordService.matches(otp, last.getOtpHash())) {
            throw new IllegalStateException("Invalid OTP.");
        }

        last.setUsed(true);
        emailOtpRepository.save(last);

        return last;
    }

    private String generate6DigitOtp() {
        int n = random.nextInt(900000) + 100000;
        return String.valueOf(n);
    }
}