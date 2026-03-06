package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BatchExtendExpiryRequest {
    @NotNull public LocalDate newExpiryDate;
}