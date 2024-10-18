package com.example.ekthacare.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ekthacare.entity.BloodDonation;

@Repository
public interface BloodDonationRepository extends JpaRepository<BloodDonation, Long> {
    
    BloodDonation findByUserId(Long userId);
    
    // Custom query to find the blood donation record by recipient ID
    List<BloodDonation> findByRecipientId(Long recipientId);
    
    // Optional: Add a method to find by both userId and recipientId if needed
    BloodDonation findByUserIdAndRecipientId(Long userId, Long recipientId);
    
   
        @Query("SELECT bd.recipientId, MAX(bd.lastDonationDate) FROM BloodDonation bd WHERE bd.recipientId IN :recipientIds GROUP BY bd.recipientId")
        List<Object[]> findLastDonationDatesByRecipientIds(@Param("recipientIds") List<Long> recipientIds);
  
    
}
