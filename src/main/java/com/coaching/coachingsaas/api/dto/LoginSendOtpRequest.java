package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginSendOtpRequest {
    @Email @NotBlank public String email;
}