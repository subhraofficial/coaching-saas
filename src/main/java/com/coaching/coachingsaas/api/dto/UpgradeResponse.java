package com.coaching.coachingsaas.api.dto;

public class UpgradeResponse {
    public String razorpayKeyId;
    public String subscriptionId;
    public int amountInr;

    public UpgradeResponse(String razorpayKeyId, String subscriptionId, int amountInr) {
        this.razorpayKeyId = razorpayKeyId;
        this.subscriptionId = subscriptionId;
        this.amountInr = amountInr;
    }
}