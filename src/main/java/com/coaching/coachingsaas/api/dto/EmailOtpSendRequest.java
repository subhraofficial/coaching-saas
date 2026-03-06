package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailOtpSendRequest {
    @Email @NotBlank
    public String email;
}