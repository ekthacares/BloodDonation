package com.example.ekthacare.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.BloodDonation;
import com.example.ekthacare.repo.BloodDonationRepository;

@Service
public class BloodDonationService {

    private final BloodDonationRepository bloodDonationRepository;

    @Autowired
    public BloodDonationService(BloodDonationRepository bloodDonationRepository) {
        this.bloodDonationRepository = bloodDonationRepository;
    }

    // Updated method to include recipientId
    public void updateLastDonationDate(Long userId, Long recipientId, LocalDateTime lastDonationDate) {
        // Retrieve the user's blood donation record
        BloodDonation bloodDonation = bloodDonationRepository.findByUserId(userId);
        
        // If a record exists, update the last donation date
        if (bloodDonation != null) {
            bloodDonation.setLastDonationDate(lastDonationDate);
            bloodDonation.setRecipientId(recipientId);  // Ensure recipientId is set
            bloodDonationRepository.save(bloodDonation);
        } else {
            // Optionally, create a new record if none exists
            bloodDonation = new BloodDonation();
            bloodDonation.setUserId(userId);
            bloodDonation.setRecipientId(recipientId);  // Ensure recipientId is set
            bloodDonation.setLastDonationDate(lastDonationDate);
            bloodDonation.setLastDonationDate(lastDonationDate);
            bloodDonationRepository.save(bloodDonation);
        }

        // Log the recipient ID as well (if needed)
        System.out.println("Updated last donation date for user ID: " + userId + " for recipient ID: " + recipientId);
    }
    
   
    
    public List<BloodDonation> getDonationsByRecipientId(Long recipientId) {
        return bloodDonationRepository.findByRecipientId(recipientId);
    }
    
    public List<BloodDonation> getAllDonations() {
        return bloodDonationRepository.findAll();
    }
    
    
    public Map<Long, LocalDateTime> getLastDonationDatesByRecipientIds(List<Long> recipientIds) {
        List<Object[]> results = bloodDonationRepository.findLastDonationDatesByRecipientIds(recipientIds);
        return results.stream()
                .collect(Collectors.toMap(
                    result -> (Long) result[0],              // recipientId
                    result -> (LocalDateTime) result[1]     // last donation date (as LocalDateTime)
                ));
    }

    public BloodDonation findDonationsByUserId(Long userId) {
        return bloodDonationRepository.findByUserId(userId);
    }

    public List<BloodDonation> findDonationsByRecipientId(Long recipientId) {
        return bloodDonationRepository.findByRecipientId(recipientId);
    }
}