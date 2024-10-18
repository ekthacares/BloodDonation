package com.example.ekthacare.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "adminotp")
public class Otp1 {
	
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)    
	    private Long id;
	    
	    @Column(name = "mobile")
	    private String mobile;
	    
	    @Column(name = "otp")
	    private String otp;
	    
	    @Column(name = "expirytime")
	    private LocalDateTime expirytime;
	    
	    

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getOtp() {
			return otp;
		}

		public void setOtp(String otp) {
			this.otp = otp;
		}

		public LocalDateTime getExpirytime() {
			return expirytime;
		}

		public void setExpirytime(LocalDateTime expirytime) {
			this.expirytime = expirytime;
		}

		@Override
		public String toString() {
			return "Otp1 [id=" + id + ", mobile=" + mobile + ", otp=" + otp + ", expirytime=" + expirytime + "]";
		}
	    
	    

}