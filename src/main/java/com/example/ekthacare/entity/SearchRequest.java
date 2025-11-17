package com.example.ekthacare.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
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

    private String area;      // NEW
    private String address;   // NEW

    @Column(name = "requested_date")
    private LocalDate requestedDate;

    private LocalDateTime timestamp;

    public SearchRequest() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getBloodgroup() { return bloodgroup; }
    public void setBloodgroup(String bloodgroup) { this.bloodgroup = bloodgroup; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp.truncatedTo(ChronoUnit.SECONDS);
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return this.timestamp.format(formatter);
    }

    @Override
    public String toString() {
        return "SearchRequest [id=" + id +
                ", userId=" + userId +
                ", bloodgroup=" + bloodgroup +
                ", city=" + city +
                ", state=" + state +
                ", area=" + area +
                ", address=" + address +
                ", requestedDate=" + requestedDate +
                ", timestamp=" + timestamp + "]";
    }
}
