package com.example.ekthacare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
public class Campaigns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    @Column(name = "date")
    private LocalDate campaignDate;
    @Column(name = "time")
    private LocalTime campaignTime;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public Campaigns() {
    }

    public Campaigns(Long id, String title, String message, LocalDate campaignDate, LocalTime campaignTime, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.campaignDate = campaignDate;
        this.campaignTime = campaignTime;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getCampaignDate() {
        return campaignDate;
    }

    public void setCampaignDate(LocalDate campaignDate) {
        this.campaignDate = campaignDate;
    }

    public LocalTime getCampaignTime() {
        return campaignTime;
    }

    public void setCampaignTime(LocalTime campaignTime) {
        this.campaignTime = campaignTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
