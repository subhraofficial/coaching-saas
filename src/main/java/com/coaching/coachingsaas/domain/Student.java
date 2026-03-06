package com.coaching.coachingsaas.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "students",
        indexes = {
                @Index(name = "idx_student_coaching", columnList = "coaching_id"),
                @Index(name = "idx_student_mobile", columnList = "mobile")
        }
)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private boolean active = true;

    // ✅ created_at column (required for carry forward logic)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // -------------------------
    // Relationships
    // -------------------------

    @ManyToOne(optional = false)
    @JoinColumn(name = "coaching_id")
    private Coaching coaching;

    // Many students can belong to many batches
    @ManyToMany
    @JoinTable(
            name = "student_batches",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "batch_id")
    )
    private Set<Batch> batches = new HashSet<>();

    // -------------------------
    // Getters & Setters
    // -------------------------

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Coaching getCoaching() {
        return coaching;
    }

    public void setCoaching(Coaching coaching) {
        this.coaching = coaching;
    }

    public Set<Batch> getBatches() {
        return batches;
    }

    public void setBatches(Set<Batch> batches) {
        this.batches = batches;
    }
}