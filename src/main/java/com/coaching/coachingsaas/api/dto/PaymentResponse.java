package com.coaching.coachingsaas.api.dto;

import java.time.LocalDateTime;

public class PaymentResponse {
    public Long id;
    public int amount;
    public String paidForMonth;
    public LocalDateTime paidAt;
    public String note;

    public PaymentResponse(Long id, int amount, String paidForMonth, LocalDateTime paidAt, String note) {
        this.id = id;
        this.amount = amount;
        this.paidForMonth = paidForMonth;
        this.paidAt = paidAt;
        this.note = note;
    }
}