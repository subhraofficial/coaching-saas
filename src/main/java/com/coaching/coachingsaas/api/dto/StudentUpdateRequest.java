package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.NotBlank;

public class StudentUpdateRequest {

    @NotBlank
    public String name;

    @NotBlank
    public String mobile;
}