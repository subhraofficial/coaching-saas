package com.coaching.coachingsaas.api.dto;

public class AdminSummaryResponse {

    public long totalCoachings;
    public long totalStudents;
    public long totalBatches;
    public long totalCollection;
    public long freeCoachings;
    public long proCoachings;
    public long activeCoachings;
    public long inactiveCoachings;

    public AdminSummaryResponse(long totalCoachings,
                                long totalStudents,
                                long totalBatches,
                                long totalCollection,
                                long freeCoachings,
                                long proCoachings,
                                long activeCoachings,
                                long inactiveCoachings) {
        this.totalCoachings = totalCoachings;
        this.totalStudents = totalStudents;
        this.totalBatches = totalBatches;
        this.totalCollection = totalCollection;
        this.freeCoachings = freeCoachings;
        this.proCoachings = proCoachings;
        this.activeCoachings = activeCoachings;
        this.inactiveCoachings = inactiveCoachings;
    }
}