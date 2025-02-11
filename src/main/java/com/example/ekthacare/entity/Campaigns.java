package com.example.ekthacare.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity

public class Campaigns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Campaigns(Long id, String title, String message, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.title = title;
		this.message = message;
		this.createdAt = createdAt;
	}

	public Campaigns() {
		// TODO Auto-generated constructor stub
	}
    
    
}
