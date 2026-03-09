package com.coaching.coachingsaas.api.dto;

public class AdminCoachingDetailResponse {

    public Long id;
    public String coachingName;
    public String ownerName;
    public String email;
    public String phone;
    public String planType;
    public boolean active;

    public long studentCount;
    public long batchCount;
    public long totalCollection;

    public AdminCoachingDetailResponse(
            Long id,
            String coachingName,
            String ownerName,
            String email,
            String phone,
            String planType,
            boolean active,
            long studentCount,
            long batchCount,
            long totalCollection
    ) {
        this.id = id;
        this.coachingName = coachingName;
        this.ownerName = ownerName;
        this.email = email;
        this.phone = phone;
        this.planType = planType;
        this.active = active;
        this.studentCount = studentCount;
        this.batchCount = batchCount;
        this.totalCollection = totalCollection;
    }
}