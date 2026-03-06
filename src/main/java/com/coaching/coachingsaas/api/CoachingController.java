package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.service.CoachingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaching")
public class CoachingController {

    private final CoachingService coachingService;

    public CoachingController(CoachingService coachingService) {
        this.coachingService = coachingService;
    }

    private Long currentCoachingId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @PostMapping("/upgrade-pro")
    public ResponseEntity<?> upgradeToPro(Authentication auth) {
        Long coachingId = currentCoachingId(auth);
        Coaching updated = coachingService.upgradeToPro(coachingId);
        return ResponseEntity.ok("Upgraded to PRO. Plan=" + updated.getPlanType());
    }
}