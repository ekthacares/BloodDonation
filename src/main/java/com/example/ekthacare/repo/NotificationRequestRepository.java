package com.example.ekthacare.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ekthacare.entity.NotificationRequest;
import com.google.api.services.storage.Storage.BucketAccessControls.List;

@Repository

	public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Long> {
	    // You can add custom query methods here if needed
	
	java.util.List<NotificationRequest> findByFcmToken(String fcmToken);
	
	}

