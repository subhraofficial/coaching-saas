package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.api.dto.*;
import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.domain.PlanType;
import com.coaching.coachingsaas.repo.CoachingRepository;
import com.coaching.coachingsaas.service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final CoachingRepository coachingRepository;
    private final RazorpayService razorpayService;

    @Value("${razorpay.pro_monthly_amount_inr}")
    private int proAmountInr;

    public BillingController(CoachingRepository coachingRepository, RazorpayService razorpayService) {
        this.coachingRepository = coachingRepository;
        this.razorpayService = razorpayService;
    }

    private Long coachingId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @GetMapping("/me")
    public BillingMeResponse me(Authentication auth) {
        Long coachingId = coachingId(auth);
        Coaching c = coachingRepository.findById(coachingId)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        return new BillingMeResponse(
                c.getPlanType().name(),
                c.getSubscriptionStatus(),
                c.getRazorpaySubscriptionId(),
                c.getCurrentPeriodEnd()
        );
    }

    // ✅ Create a new Razorpay Subscription and return subscriptionId to Flutter
    @PostMapping("/upgrade/pro")
    public UpgradeResponse upgradeToPro(Authentication auth) {
        Long coachingId = coachingId(auth);
        Coaching c = coachingRepository.findById(coachingId)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        // If already PRO active, no need to create again
        if (c.getPlanType() == PlanType.PRO && "ACTIVE".equalsIgnoreCase(c.getSubscriptionStatus())) {
            throw new RuntimeException("Already PRO");
        }

        String subId = razorpayService.createProMonthlySubscription(coachingId);

        c.setRazorpaySubscriptionId(subId);
        c.setSubscriptionStatus("CREATED");
        coachingRepository.save(c);

        return new UpgradeResponse(razorpayService.getKeyId(), subId, proAmountInr);
    }

    // ✅ Optional: verify checkout response signature (good for extra safety)
    @PostMapping("/verify")
    public String verify(@Valid @RequestBody VerifySubscriptionRequest req, Authentication auth) {
        Long coachingId = coachingId(auth);
        Coaching c = coachingRepository.findById(coachingId)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        // Must match what we created for this coaching
        if (c.getRazorpaySubscriptionId() == null ||
                !c.getRazorpaySubscriptionId().equals(req.razorpay_subscription_id)) {
            throw new RuntimeException("Subscription mismatch");
        }

        boolean ok = razorpayService.verifySubscriptionSignature(
                req.razorpay_subscription_id,
                req.razorpay_payment_id,
                req.razorpay_signature
        );

        if (!ok) throw new RuntimeException("Invalid signature");

        // Mark as AUTHENTICATED (webhook will set ACTIVE)
        c.setSubscriptionStatus("AUTHENTICATED");
        coachingRepository.save(c);

        return "OK";
    }
    @PostMapping("/dev/activate-pro")
    public String devActivatePro(Authentication auth) {
        Long coachingId = coachingId(auth);

        Coaching c = coachingRepository.findById(coachingId)
                .orElseThrow(() -> new RuntimeException("Coaching not found"));

        c.setPlanType(PlanType.PRO);
        c.setSubscriptionStatus("DEV_ACTIVE");
        coachingRepository.save(c);

        return "PRO activated (DEV mode)";
    }
}