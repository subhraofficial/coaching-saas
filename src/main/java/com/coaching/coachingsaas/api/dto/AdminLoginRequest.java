package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminLoginRequest {

    @NotBlank
    public String email;

    @NotBlank
    public String password;
}