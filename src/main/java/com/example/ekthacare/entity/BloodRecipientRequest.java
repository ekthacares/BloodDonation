package com.example.ekthacare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "blood_recipient_request")  // table name
public class BloodRecipientRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;          // who is requesting blood

    private String bloodgroup;
    private String city;
    private String state;
    private String area;
    private String address;

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "requested_date")
    private LocalDate requestedDate;   // when blood is needed

    @Column(name = "created_at")
    private LocalDateTime createdAt;   // when the search was saved

    @Column(name = "donors_count")
    private Integer donorsCount;       // how many donors matched

    @Column(length = 1000)
    private String message;            // extra message / notes

    public BloodRecipientRequest() {}

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
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

    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getHospitalName() {
        return hospitalName;
    }
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }
    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null
                ? createdAt.truncatedTo(ChronoUnit.SECONDS)
                : null;
    }

    public Integer getDonorsCount() {
        return donorsCount;
    }
    public void setDonorsCount(Integer donorsCount) {
        this.donorsCount = donorsCount;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    // Auto-set createdAt before insert
    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        }
    }

    @Override
    public String toString() {
        return "BloodRecipientRequest{" +
                "id=" + id +
                ", userId=" + userId +
                ", bloodgroup='" + bloodgroup + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", area='" + area + '\'' +
                ", address='" + address + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", requestedDate=" + requestedDate +
                ", createdAt=" + createdAt +
                ", donorsCount=" + donorsCount +
                ", message='" + message + '\'' +
                '}';
    }
}
