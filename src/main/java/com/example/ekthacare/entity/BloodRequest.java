package com.example.ekthacare.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String bloodgroup;
    private String city;
    private String state;
    private String country;
    private String mobile;

    public BloodRequest() {
    }

    public BloodRequest(String name, String bloodgroup, String city, String state, String country, String mobile) {
        this.name = name;
        this.bloodgroup = bloodgroup;
        this.city = city;
        this.state = state;
        this.country = country;
        this.mobile = mobile;
    }
    
    
    // Getters and Setters


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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "BloodRequest [id=" + id + ", name=" + name + ", bloodgroup=" + bloodgroup + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", mobile=" + mobile + "]";
	}

    
    
}
