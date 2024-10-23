package com.example.ekthacare.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class SearchRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String bloodgroup;
    private String city;
    private String state;
    private LocalDateTime timestamp; // Keep as LocalDateTime for internal processing

    public SearchRequest() {
        // Default constructor
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp.truncatedTo(ChronoUnit.SECONDS); // Ensure timestamp has no milliseconds/nanoseconds
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); // Set current time without milliseconds/nanoseconds
    }

    
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return this.timestamp.format(formatter);
    }


    @Override
    public String toString() {
        return "SearchRequest [id=" + id + ", userId=" + userId + ", bloodgroup=" + bloodgroup + ", city=" + city
                + ", state=" + state + ", timestamp=" + timestamp + "]";
    }
}
