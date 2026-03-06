package com.coaching.coachingsaas.service;

import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.domain.PlanType;
import com.coaching.coachingsaas.repo.CoachingRepository;
import org.springframework.stereotype.Service;

@Service
public class CoachingService {

    private final CoachingRepository coachingRepository;

    public CoachingService(CoachingRepository coachingRepository) {
        this.coachingRepository = coachingRepository;
    }

    public Coaching upgradeToPro(Long coachingId) {
        Coaching c = coachingRepository.findById(coachingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coachingId"));

        c.setPlanType(PlanType.PRO);
        c.setStudentLimit(null); // unlimited
        return coachingRepository.save(c);
    }
}