package com.coaching.coachingsaas.api.dto;

public class FeesStudentRowResponse {
    public Long studentId;
    public String name;
    public String mobile;
    public int monthlyDue;
    public int paidThisMonth;
    public int pending;

    public FeesStudentRowResponse(Long studentId, String name, String mobile, int monthlyDue, int paidThisMonth, int pending) {
        this.studentId = studentId;
        this.name = name;
        this.mobile = mobile;
        this.monthlyDue = monthlyDue;
        this.paidThisMonth = paidThisMonth;
        this.pending = pending;
    }
}