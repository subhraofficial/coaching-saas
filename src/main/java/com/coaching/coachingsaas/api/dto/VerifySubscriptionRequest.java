package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifySubscriptionRequest {
    @NotBlank public String razorpay_subscription_id;
    @NotBlank public String razorpay_payment_id;
    @NotBlank public String razorpay_signature;
}