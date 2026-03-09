package com.coaching.coachingsaas.api.dto;

public class AdminCoachingListItemResponse {

    public Long id;
    public String coachingName;
    public String ownerName;
    public String email;
    public String planType;
    public boolean active;
    public long studentCount;

    public AdminCoachingListItemResponse(Long id,
                                         String coachingName,
                                         String ownerName,
                                         String email,
                                         String planType,
                                         boolean active,
                                         long studentCount) {
        this.id = id;
        this.coachingName = coachingName;
        this.ownerName = ownerName;
        this.email = email;
        this.planType = planType;
        this.active = active;
        this.studentCount = studentCount;
    }
}