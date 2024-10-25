package com.example.ekthacare.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class SentEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private LocalDateTime sentAt;
    private String confirmationUrl;
    private Long recipientId;
    private Long loggedInUserId;

    // Default constructor
    public SentEmail() {
    }

    public SentEmail(Long id, String email, LocalDateTime sentAt, String confirmationUrl, Long recipientId, Long loggedInUserId) {
        this.id = id;
        this.email = email;
        this.setSentAt(sentAt);  // Ensures timestamp precision is set when value is assigned
        this.confirmationUrl = confirmationUrl;
        this.recipientId = recipientId;
        this.loggedInUserId = loggedInUserId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt != null ? sentAt.truncatedTo(ChronoUnit.SECONDS) : null;
    }

    public String getConfirmationUrl() {
        return confirmationUrl;
    }

    public void setConfirmationUrl(String confirmationUrl) {
        this.confirmationUrl = confirmationUrl;
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

    @PrePersist
    public void setSentAtTimestamp() {
        this.sentAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public String getFormattedSentAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return this.sentAt != null ? this.sentAt.format(formatter) : "";
    }

    @Override
    public String toString() {
        return "SentEmail [id=" + id + ", email=" + email + ", sentAt=" + sentAt + ", confirmationUrl=" + confirmationUrl
                + ", recipientId=" + recipientId + ", loggedInUserId=" + loggedInUserId + "]";
    }
}
