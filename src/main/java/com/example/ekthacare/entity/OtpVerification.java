package com.example.ekthacare.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tracking_otp")
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recipientId;
    private Long loggedInUserId;
    private String otp;
    private LocalDateTime generatedAt;
    private LocalDateTime expirationTime;

    // Constructors
    public OtpVerification(Long recipientId, Long loggedInUserId, String otp, LocalDateTime expirationTime) {
        this.recipientId = recipientId;
        this.loggedInUserId = loggedInUserId;
        this.otp = otp;
        this.generatedAt = LocalDateTime.now();
        this.expirationTime = expirationTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(Long loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "OtpVerification [id=" + id + ", recipientId=" + recipientId + ", loggedInUserId=" + loggedInUserId
                + ", otp=" + otp + ", generatedAt=" + generatedAt + ", expirationTime=" + expirationTime + "]";
    }
    
    // Default constructor (no-arg constructor)
    public OtpVerification() {
        // JPA requires a no-argument constructor
    }

}
