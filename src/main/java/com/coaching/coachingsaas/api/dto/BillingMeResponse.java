package com.coaching.coachingsaas.api.dto;

import java.time.LocalDateTime;

public class BillingMeResponse {
    public String planType;
    public String subscriptionStatus;
    public String razorpaySubscriptionId;
    public LocalDateTime currentPeriodEnd;

    public BillingMeResponse(String planType, String subscriptionStatus, String razorpaySubscriptionId, LocalDateTime currentPeriodEnd) {
        this.planType = planType;
        this.subscriptionStatus = subscriptionStatus;
        this.razorpaySubscriptionId = razorpaySubscriptionId;
        this.currentPeriodEnd = currentPeriodEnd;
    }
}