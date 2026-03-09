package com.coaching.coachingsaas.repo;

import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.domain.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoachingRepository extends JpaRepository<Coaching, Long> {

    Optional<Coaching> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Coaching> findByRazorpaySubscriptionId(String razorpaySubscriptionId);

    List<Coaching> findAllByOrderByIdDesc();

    long countByPlanType(PlanType planType);

    long countByActiveTrue();

    long countByActiveFalse();
}