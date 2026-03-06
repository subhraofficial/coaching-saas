package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PaymentCreateRequest {

    @NotNull public Long studentId;

    @Min(1) public int amount;

    // "YYYY-MM"
    @NotBlank
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}$", message = "paidForMonth must be YYYY-MM")
    public String paidForMonth;

    public String note;
}