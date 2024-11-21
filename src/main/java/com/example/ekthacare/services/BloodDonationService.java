package com.example.ekthacare.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.BloodDonation;
import com.example.ekthacare.repo.BloodDonationRepository;

@Service
public class BloodDonationService {

    private final BloodDonationRepository bloodDonationRepository;

    public BloodDonationService(BloodDonationRepository bloodDonationRepository) {
        this.bloodDonationRepository = bloodDonationRepository;
    }

    // Updated method to include hospitalName
    public void updateLastDonationDate(Long userId, Long recipientId, LocalDateTime lastDonationDate, String hospitalName) {
        // Retrieve the user's blood donation record
        BloodDonation bloodDonation = bloodDonationRepository.findByUserId(userId);

        if (bloodDonation != null) {
            // Update the existing donation record
            bloodDonation.setLastDonationDate(lastDonationDate);
            bloodDonation.setRecipientId(recipientId);
            bloodDonation.setHospitalName(hospitalName); // Set hospital name
            bloodDonationRepository.save(bloodDonation);

            // Log the update
            System.out.println("Updated last donation date for user ID: " + userId + 
                               ", recipient ID: " + recipientId +
                               ", hospital name: " + hospitalName);
        } else {
            // Create a new record if none exists
            bloodDonation = new BloodDonation();
            bloodDonation.setUserId(userId);
            bloodDonation.setRecipientId(recipientId);
            bloodDonation.setLastDonationDate(lastDonationDate);
            bloodDonation.setHospitalName(hospitalName); // Set hospital name
            bloodDonationRepository.save(bloodDonation);

            // Log the creation
            System.out.println("Created new donation record for user ID: " + userId + 
                               ", recipient ID: " + recipientId +
                               ", hospital name: " + hospitalName);
        }
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
    
    public List<BloodDonation> getAllDonations(int page, int pageSize) {
        return bloodDonationRepository.findAll(PageRequest.of(page, pageSize)).getContent();
    }

    public long countAllDonations() {
        return bloodDonationRepository.count();
    }
}
