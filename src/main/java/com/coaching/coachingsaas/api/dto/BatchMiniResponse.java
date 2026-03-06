package com.coaching.coachingsaas.api.dto;

public class BatchMiniResponse {
    public Long id;
    public String name;
    public int monthlyFee;

    public BatchMiniResponse(Long id, String name, int monthlyFee) {
        this.id = id;
        this.name = name;
        this.monthlyFee = monthlyFee;
    }
}