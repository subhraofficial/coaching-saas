package com.coaching.coachingsaas.repo;

import com.coaching.coachingsaas.domain.Coaching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoachingRepository extends JpaRepository<Coaching, Long> {
    Optional<Coaching> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Coaching> findByRazorpaySubscriptionId(String razorpaySubscriptionId);
}