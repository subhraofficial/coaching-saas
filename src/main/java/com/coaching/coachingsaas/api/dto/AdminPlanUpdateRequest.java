package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminPlanUpdateRequest {

    @NotBlank
    public String planType;
}