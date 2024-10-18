package com.example.ekthacare.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

    // Constructors, Getters, and Setters 
    
        
   

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
		this.sentAt = sentAt;
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



	public SentEmail(Long id, String email, LocalDateTime sentAt, String confirmationUrl, Long recipientId,
			Long loggedInUserId) {
		super();
		this.id = id;
		this.email = email;
		this.sentAt = sentAt;
		this.confirmationUrl = confirmationUrl;
		this.recipientId = recipientId;
		this.loggedInUserId = loggedInUserId;
	}

	public SentEmail() {
    }

	
}
