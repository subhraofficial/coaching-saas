package com.coaching.coachingsaas.service;

import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.repo.CoachingRepository;
import org.springframework.stereotype.Service;

@Service
public class CoachingAuthService {

    private final CoachingRepository coachingRepository;

    public CoachingAuthService(CoachingRepository coachingRepository) {
        this.coachingRepository = coachingRepository;
    }

    public Coaching getOrThrowByEmail(String email) {
        return coachingRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Email not registered. Please register first."));
    }

    public Coaching createIfNotExists(String coachingName, String ownerName, String phone, String email) {
        return coachingRepository.findByEmail(email).orElseGet(() -> {
            Coaching c = new Coaching();
            c.setCoachingName(coachingName);
            c.setOwnerName(ownerName);
            c.setPhone(phone);
            c.setEmail(email);

            // Not used for OTP login but required
            c.setPasswordHash("OTP_ONLY");

            return coachingRepository.save(c);
        });
    }
}