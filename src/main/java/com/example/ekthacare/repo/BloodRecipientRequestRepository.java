package com.example.ekthacare.repo;


import com.example.ekthacare.entity.BloodRecipientRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodRecipientRequestRepository extends JpaRepository<BloodRecipientRequest, Long> {
	

    BloodRecipientRequest findTopByUserIdOrderByRequestedDateDesc(Long userId);
}
