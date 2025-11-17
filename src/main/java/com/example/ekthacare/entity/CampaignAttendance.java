package com.example.ekthacare.entity;


import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "campaign_attendance")
public class CampaignAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;         // who clicked "I AM IN"

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;     // related campaign ID

    @Column(length = 255)
    private String message;      // Example: "I AM IN"

    @Column(name = "clicked_date")
    private LocalDate date;  // When user clicked

    // Default constructor
    public CampaignAttendance() {}

    // Constructor with fields
    public CampaignAttendance(Long userId, Long campaignId, String message, LocalDate date) {
        this.userId = userId;
        this.campaignId = campaignId;
        this.message = message;
        this.date = date;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
