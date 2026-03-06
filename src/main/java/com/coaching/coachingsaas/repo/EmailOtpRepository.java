package com.coaching.coachingsaas.repo;

import com.coaching.coachingsaas.domain.EmailOtp;
import com.coaching.coachingsaas.domain.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);
}