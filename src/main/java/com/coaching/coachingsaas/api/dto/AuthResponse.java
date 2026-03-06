package com.coaching.coachingsaas.api.dto;

public class AuthResponse {
    public Long coachingId;
    public String token;
    public String planType;

    public AuthResponse(Long coachingId, String token, String planType) {
        this.coachingId = coachingId;
        this.token = token;
        this.planType = planType;
    }
}