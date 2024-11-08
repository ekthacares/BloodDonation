package com.example.ekthacare.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "superadmin")
public class Superadmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "emailid")
    private String emailid;

   
    
    
    // No-argument constructor for JPA
    public Superadmin() {
    }

    public Superadmin(Long id, String name, String mobile, String emailid) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.emailid = emailid;
        
    }

    // Getters and setters...

    @Override
    public String toString() {
        return "Donor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", emailid='" + emailid + '\'' +
                 '}';
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

   

	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	
	

	

	
}
