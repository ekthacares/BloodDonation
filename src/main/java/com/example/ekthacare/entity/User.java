package com.example.ekthacare.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "donors")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "donorname")
    private String donorname;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "emailid")
    private String emailid;

    @Column(name = "dateofbirth")
    private LocalDate dateofbirth;

    @Column(name = "bloodgroup")
    private String bloodgroup;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "isverified")
    private boolean isverified;
    
    // New fields for tracking creation, update, and deletion
    @Column(name = "created_by")
    private Long createdBy;  // Changed from String to Long
    
    private String createdByType; // The type of the creator ("admin" or "donor")

    @Column(name = "updated_by")
    private Long updatedBy;  // Changed from String to Long

    @Column(name = "deleted_by")
    private Long deletedBy;  // Changed from String to Long
    
    private String deletedByType; // The type of the creator ("admin" or "donor")

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private boolean isDeleted = false;
    // No-argument constructor for JPA
    public User() {
    }

    public User(Long id, String donorname, String mobile, String emailid, LocalDate dateofbirth,
            String bloodgroup, Integer age, String gender, String address, String city, String state,
            boolean isverified, Long createdBy, Long updatedBy, Long deletedBy,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
    this.id = id;
    this.donorname = donorname;
    this.mobile = mobile;
    this.emailid = emailid;
    this.dateofbirth = dateofbirth;
    this.bloodgroup = bloodgroup;
    this.age = age;
    this.gender = gender;
    this.address = address;
    this.city = city;
    this.state = state;
    this.isverified = isverified;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
    this.deletedBy = deletedBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.deletedAt = deletedAt;
}

    // Getters and setters...

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", donorname='" + donorname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", emailid='" + emailid + '\'' +
                ", dateofbirth=" + dateofbirth +
                ", bloodgroup='" + bloodgroup + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", isverified=" + isverified +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", deletedBy='" + deletedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDonorname() {
        return donorname;
    }

    public void setDonorname(String donorname) {
        this.donorname = donorname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public LocalDate getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(LocalDate dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isverified() {
        return isverified;
    }

    public void setIsVerified(boolean isverified) {
        this.isverified = isverified;
    }

	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public String getCreatedByType() {
        return createdByType;
    }

    public void setCreatedByType(String createdByType) {
        this.createdByType = createdByType;
    }
	
    public String getDeletedByType() {
        return deletedByType;
    }

    public void setDeletedByType(String deletedByType) {
        this.deletedByType = deletedByType;
    }
}
