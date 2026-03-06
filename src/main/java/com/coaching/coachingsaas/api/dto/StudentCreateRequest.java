package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class StudentCreateRequest {
    @NotBlank public String name;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be 10 digits")
    public String mobile;

    // student can be in 1+ batches
    @NotEmpty public List<Long> batchIds;
}