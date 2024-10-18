package com.example.ekthacare.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId; // ID of the user whose profile is changed
    private String changedField; // The field that was changed
    private String oldValue; // Old value of the field
    private String newValue; // New value of the field
    private LocalDateTime changedAt; // Timestamp of the change
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getChangedField() {
		return changedField;
	}
	public void setChangedField(String changedField) {
		this.changedField = changedField;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public LocalDateTime getChangedAt() {
		return changedAt;
	}
	public void setChangedAt(LocalDateTime changedAt) {
		this.changedAt = changedAt;
	}
	public ChangeLog(Long id, Long userId, String changedField, String oldValue, String newValue,
			LocalDateTime changedAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.changedField = changedField;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.changedAt = changedAt;
	}
	public ChangeLog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "ChangeLog [id=" + id + ", userId=" + userId + ", changedField=" + changedField + ", oldValue="
				+ oldValue + ", newValue=" + newValue + ", changedAt=" + changedAt + "]";
	}


    
}
