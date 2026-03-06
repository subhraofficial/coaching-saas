package com.coaching.coachingsaas.api.dto;

import java.time.LocalDate;

public class BatchResponse {
    public Long id;
    public String name;
    public int monthlyFee;
    public LocalDate startDate;
    public LocalDate expiryDate;

    public BatchResponse(Long id, String name, int monthlyFee, LocalDate startDate, LocalDate expiryDate) {
        this.id = id;
        this.name = name;
        this.monthlyFee = monthlyFee;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
    }
}