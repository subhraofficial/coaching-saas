package com.coaching.coachingsaas.api.dto;

import java.util.List;

public class FeesSummaryResponse {
    public String month; // YYYY-MM
    public int totalPending;
    public List<FeesStudentRowResponse> students;

    public FeesSummaryResponse(String month, int totalPending, List<FeesStudentRowResponse> students) {
        this.month = month;
        this.totalPending = totalPending;
        this.students = students;
    }
}