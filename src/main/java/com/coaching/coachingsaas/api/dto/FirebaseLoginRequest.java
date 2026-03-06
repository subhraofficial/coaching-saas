package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.NotBlank;

public class FirebaseLoginRequest {
    @NotBlank
    public String idToken; // Firebase ID token from Flutter
}