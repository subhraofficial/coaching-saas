package com.coaching.coachingsaas.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_coaching", columnList = "coaching_id"),
        @Index(name = "idx_payment_student", columnList = "student_id"),
        @Index(name = "idx_payment_month", columnList = "paid_for_month")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "coaching_id")
    private Coaching coaching;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false)
    private int amount;

    // "YYYY-MM" (example: 2026-02)
    @Column(name = "paid_for_month", nullable = false, length = 7)
    private String paidForMonth;

    @Column(nullable = false)
    private LocalDateTime paidAt = LocalDateTime.now();

    private String note;

    public Long getId() { return id; }

    public Coaching getCoaching() { return coaching; }
    public void setCoaching(Coaching coaching) { this.coaching = coaching; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getPaidForMonth() { return paidForMonth; }
    public void setPaidForMonth(String paidForMonth) { this.paidForMonth = paidForMonth; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}