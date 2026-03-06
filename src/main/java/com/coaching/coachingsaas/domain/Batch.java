package com.coaching.coachingsaas.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "batches", indexes = {
        @Index(name = "idx_batch_coaching", columnList = "coaching_id")
})
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private int monthlyFee;

    @Column(nullable=false)
    private LocalDate startDate;

    @Column(nullable=false)
    private LocalDate expiryDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "coaching_id")
    private Coaching coaching;

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(int monthlyFee) { this.monthlyFee = monthlyFee; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Coaching getCoaching() { return coaching; }
    public void setCoaching(Coaching coaching) { this.coaching = coaching; }
}