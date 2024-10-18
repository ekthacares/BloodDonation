package com.example.ekthacare.entity;

import java.time.LocalDate;
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
    
    
    // No-argument constructor for JPA
    public User() {
    }

    public User(Long id, String donorname, String mobile, String emailid, LocalDate dateofbirth,
                 String bloodgroup, Integer age, String gender, String address, String city, String state,
                 boolean isverified) {
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
    }

    // Getters and setters...

    @Override
    public String toString() {
        return "Donor{" +
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

    public void setVerified(boolean isverified) {
        this.isverified = isverified;
    }

	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	
	

	

	
}
