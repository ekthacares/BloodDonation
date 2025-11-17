package com.example.ekthacare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin")
public class User1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")    // Admin name (similar to donorname)
    private String name;

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

    // SAME FIELDS AS DONOR USER

    @Column(name = "created_by")
    private Long createdBy;

    private String createdByType;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_by")
    private Long deletedBy;

    private String deletedByType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "profile_path", columnDefinition = "LONGTEXT")
    private String profileImage;

    private boolean isDeleted = false;

    @Column(name = "jwt_token")
    private String jwtToken;

    @Column(name = "fcm_token")
    private String fcmToken;

    // No-argument constructor
    public User1() {}

    // Full Constructor
    public User1(Long id, String name, String mobile, String emailid, LocalDate dateofbirth,
                 String bloodgroup, Integer age, String gender, String address, String city, String state,
                 boolean isverified, Long createdBy, String createdByType, Long updatedBy, Long deletedBy,
                 String deletedByType, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
                 String profileImage, boolean isDeleted, String jwtToken, String fcmToken) {

        this.id = id;
        this.name = name;
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
        this.createdByType = createdByType;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
        this.deletedByType = deletedByType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.profileImage = profileImage;
        this.isDeleted = isDeleted;
        this.jwtToken = jwtToken;
        this.fcmToken = fcmToken;
    }

    // Getters and Setters (copy exact pattern of User)
    // -------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmailid() { return emailid; }
    public void setEmailid(String emailid) { this.emailid = emailid; }

    public LocalDate getDateofbirth() { return dateofbirth; }
    public void setDateofbirth(LocalDate dateofbirth) { this.dateofbirth = dateofbirth; }

    public String getBloodgroup() { return bloodgroup; }
    public void setBloodgroup(String bloodgroup) { this.bloodgroup = bloodgroup; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public boolean isverified() { return isverified; }
    public void setVerified(boolean isverified) { this.isverified = isverified; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public String getCreatedByType() { return createdByType; }
    public void setCreatedByType(String createdByType) { this.createdByType = createdByType; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Long getDeletedBy() { return deletedBy; }
    public void setDeletedBy(Long deletedBy) { this.deletedBy = deletedBy; }

    public String getDeletedByType() { return deletedByType; }
    public void setDeletedByType(String deletedByType) { this.deletedByType = deletedByType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getJwtToken() { return jwtToken; }
    public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    @Override
    public String toString() {
        return "User1 [id=" + id + ", name=" + name + ", mobile=" + mobile + ", emailid=" + emailid
                + ", dateofbirth=" + dateofbirth + ", bloodgroup=" + bloodgroup + ", age=" + age + ", gender=" + gender
                + ", address=" + address + ", city=" + city + ", state=" + state + ", isverified=" + isverified
                + ", createdBy=" + createdBy + ", createdByType=" + createdByType + ", updatedBy=" + updatedBy
                + ", deletedBy=" + deletedBy + ", deletedByType=" + deletedByType + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + ", deletedAt=" + deletedAt + ", profileImage=" + profileImage
                + ", isDeleted=" + isDeleted + ", jwtToken=" + jwtToken + ", fcmToken=" + fcmToken + "]";
    }
}
