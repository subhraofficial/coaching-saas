package com.coaching.coachingsaas.api.dto;

public class StudentListItemResponse {
    public Long id;
    public String name;
    public String mobile;
    public boolean active;

    public StudentListItemResponse(Long id, String name, String mobile, boolean active) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.active = active;
    }
}