package com.example.ekthacare.repo;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ekthacare.entity.SentEmail;

public interface SentEmailRepository extends JpaRepository<SentEmail, Long> {
	
	 boolean existsByEmail(String email);
	 
	 List<SentEmail> findByLoggedInUserId(Long loggedInUserId);
	    List<SentEmail> findByRecipientIdAndLoggedInUserId(Long recipientId, Long loggedInUserId);

	    List<SentEmail> findByRecipientId(Long recipientId);
	    
	    
}

