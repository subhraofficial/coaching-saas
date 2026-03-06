package com.coaching.coachingsaas.api.dto;

public class DashboardSummaryResponse {
    public String coachingName;
    public String ownerName;

    public long totalStudents;
    public long totalBatches;
    public long pendingFees;

    public DashboardSummaryResponse(String coachingName, String ownerName,
                                    long totalStudents, long totalBatches, long pendingFees) {
        this.coachingName = coachingName;
        this.ownerName = ownerName;
        this.totalStudents = totalStudents;
        this.totalBatches = totalBatches;
        this.pendingFees = pendingFees;
    }
}