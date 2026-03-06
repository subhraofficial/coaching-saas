package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterSendOtpRequest {
    @NotBlank public String coachingName;
    @NotBlank public String ownerName;
    @NotBlank public String phone;
    @Email @NotBlank public String email;
}