package com.example.ekthacare.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Confirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId;
    private Long loggedInUserId;
    private LocalDateTime confirmedAt;
    private LocalDateTime startedAt;
    private LocalDateTime stoppedAt;
    @Column(name = "hospital_name")
    private String hospitalName;  // Ensure the hospitalName field is included

    // Getters and setters...

    public boolean isConfirmed() {
        return confirmedAt != null;
    }

    public boolean isStarted() {
        return startedAt != null;
    }

    public boolean isCompleted() {
        return stoppedAt != null;
    }
    
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getFormattedConfirmedAt() {
        return confirmedAt != null ? confirmedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";
    }

	public void setConfirmedAt(LocalDateTime confirmedAt) {
		this.confirmedAt = confirmedAt;
	}

	public String getFormattedStartedAt() {
        return startedAt != null ? startedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";
    }
	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}
	
	 public LocalDateTime getStoppedAt() {
	        return stoppedAt;
	    }


	public String getFormattedStoppedAt() {
        return stoppedAt != null ? stoppedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "";
    }

	public void setStoppedAt(LocalDateTime stoppedAt) {
		this.stoppedAt = stoppedAt;
	}
	
	 public String getHospitalName() {
	        return hospitalName;
	    }

	    public void setHospitalName(String hospitalName) {
	        this.hospitalName = hospitalName;
	    }

	@Override
	public String toString() {
		return "Confirmation [id=" + id + ", recipientId=" + recipientId + ", loggedInUserId=" + loggedInUserId
				+ ", confirmedAt=" + confirmedAt + ", startedAt=" + startedAt + ", stoppedAt=" + stoppedAt
				+ ", confirmed=" + confirmed + ", hospitalName=" + hospitalName + "]";
	}

	
	private boolean confirmed;
	
	
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public Confirmation(Long id, Long recipientId, Long loggedInUserId, LocalDateTime confirmedAt,
			LocalDateTime startedAt, LocalDateTime stoppedAt, boolean confirmed, String hospitalName) {
		super();
		this.id = id;
		this.recipientId = recipientId;
		this.loggedInUserId = loggedInUserId;
		this.confirmedAt = confirmedAt;
		this.startedAt = startedAt;
		this.stoppedAt = stoppedAt;
		this.confirmed = confirmed;
		this.hospitalName = hospitalName;
	}

	public Confirmation() {
		// TODO Auto-generated constructor stub
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFormattedTimestamp(String format) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}