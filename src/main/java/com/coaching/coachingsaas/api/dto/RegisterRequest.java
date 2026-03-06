package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank public String coachingName;
    @NotBlank public String ownerName;
    @Email @NotBlank public String email;
    @NotBlank public String phone;
    @NotBlank public String password;
}