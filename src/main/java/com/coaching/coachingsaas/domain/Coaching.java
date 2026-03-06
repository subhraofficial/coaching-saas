package com.coaching.coachingsaas.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "coachings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
public class Coaching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String coachingName;

    @NotBlank
    private String ownerName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private PlanType planType = PlanType.FREE;

    /**
     * FREE: 50
     * PRO:  null (unlimited)
     */
    private Integer studentLimit = 50;

    public Long getId() { return id; }

    public String getCoachingName() { return coachingName; }
    public void setCoachingName(String coachingName) { this.coachingName = coachingName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public PlanType getPlanType() { return planType; }
    public void setPlanType(PlanType planType) { this.planType = planType; }

    public Integer getStudentLimit() { return studentLimit; }
    public void setStudentLimit(Integer studentLimit) { this.studentLimit = studentLimit; }

    // -------- Billing / Subscription (Razorpay) --------
    private String razorpaySubscriptionId;   // sub_xxxxx
    private String subscriptionStatus;       // CREATED / AUTHENTICATED / ACTIVE / CANCELLED / PAUSED / EXPIRED
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;

    public String getRazorpaySubscriptionId() { return razorpaySubscriptionId; }
    public void setRazorpaySubscriptionId(String razorpaySubscriptionId) { this.razorpaySubscriptionId = razorpaySubscriptionId; }

    public String getSubscriptionStatus() { return subscriptionStatus; }
    public void setSubscriptionStatus(String subscriptionStatus) { this.subscriptionStatus = subscriptionStatus; }

    public LocalDateTime getCurrentPeriodStart() { return currentPeriodStart; }
    public void setCurrentPeriodStart(LocalDateTime currentPeriodStart) { this.currentPeriodStart = currentPeriodStart; }

    public LocalDateTime getCurrentPeriodEnd() { return currentPeriodEnd; }
    public void setCurrentPeriodEnd(LocalDateTime currentPeriodEnd) { this.currentPeriodEnd = currentPeriodEnd; }
}