package com.coaching.coachingsaas.api.dto;

import java.util.List;

public class StudentDetailResponse {
    public Long id;
    public String name;
    public String mobile;
    public boolean active;
    public List<BatchMiniResponse> batches;

    public StudentDetailResponse(Long id, String name, String mobile, boolean active, List<BatchMiniResponse> batches) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.active = active;
        this.batches = batches;
    }
}