package com.coaching.coachingsaas.api.dto;

public class AdminAuthResponse {

    public String token;
    public String role;

    public AdminAuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }
}