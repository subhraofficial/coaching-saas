package com.coaching.coachingsaas.api.dto;

public class DefaulterStudentResponse {
    public Long studentId;
    public String name;
    public String mobile;
    public int monthlyDue;
    public int paidThisMonth;
    public int pendingAmount;
    public int overdueMonths;

    public DefaulterStudentResponse(Long studentId,
                                    String name,
                                    String mobile,
                                    int monthlyDue,
                                    int paidThisMonth,
                                    int pendingAmount,
                                    int overdueMonths) {
        this.studentId = studentId;
        this.name = name;
        this.mobile = mobile;
        this.monthlyDue = monthlyDue;
        this.paidThisMonth = paidThisMonth;
        this.pendingAmount = pendingAmount;
        this.overdueMonths = overdueMonths;
    }
}