package com.example.ekthacare.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.Confirmation;
import com.example.ekthacare.repo.ConfirmationRepository;

import jakarta.transaction.Transactional;

@Service
public class ConfirmationService {

    @Autowired
    private ConfirmationRepository confirmationRepository;

    public Confirmation getConfirmation(Long recipientId, Long loggedInUserId) {
        return confirmationRepository.findByRecipientIdAndLoggedInUserId(recipientId, loggedInUserId);
    }

    @Transactional
    public boolean saveConfirmation(Long recipientId, Long loggedInUserId, String hospitalName) {
        // Retrieve any existing confirmation for the recipient
        Confirmation existingConfirmation = confirmationRepository
                .findByRecipientIdAndLoggedInUserId(recipientId, loggedInUserId);

        if (existingConfirmation != null) {
            // Record already exists, handle accordingly (e.g., update or return a message)
            return false;
        }

        // Create new Confirmation object
        Confirmation confirmation = new Confirmation();
        confirmation.setRecipientId(recipientId);
        confirmation.setLoggedInUserId(loggedInUserId);
        confirmation.setHospitalName(hospitalName);  // Set the hospital name
        confirmation.setConfirmedAt(LocalDateTime.now());
        confirmation.setConfirmed(true);
        
        // Print the values to the console before saving
        System.out.println("Saving Confirmation with the following details:");
        System.out.println("Recipient ID: " + recipientId );
        System.out.println("Logged-in User ID: " + loggedInUserId);
        System.out.println("Hospital Name: " + hospitalName);
        System.out.println("Confirmed At: " + confirmation.getFormattedConfirmedAt());

        // Save the confirmation with the hospital name included
        confirmationRepository.save(confirmation);

        return true;
    }

  

    public boolean startDonation(Long recipientId, Long loggedInUserId) {
        try {
            // Find the latest confirmation for the recipient and logged-in user
            Confirmation lastConfirmation = confirmationRepository
                    .findFirstByRecipientIdAndLoggedInUserIdOrderByStoppedAtDesc(recipientId, loggedInUserId);

            if (lastConfirmation != null) {
                String formattedStoppedAt = lastConfirmation.getFormattedStoppedAt();
                if (formattedStoppedAt != null && !formattedStoppedAt.isEmpty()) {
                    // Define the date format used in your stoppedAt string
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss");

                    // Parse the stoppedAt string into LocalDateTime
                    LocalDateTime stoppedAtDate = LocalDateTime.parse(formattedStoppedAt, formatter);

                    // Calculate the date three months ago from now
                    LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

                    if (stoppedAtDate.isAfter(threeMonthsAgo)) {
                        // If the last donation was within the last 3 months, do not allow donation to start
                        return false;
                    }
                }
            }

            // Proceed with starting the donation if no active restrictions
            Confirmation confirmation = getConfirmationByUserIds(recipientId, loggedInUserId);
            if (confirmation != null && confirmation.isConfirmed()) {
                confirmation.setStartedAt(LocalDateTime.now());
                confirmationRepository.save(confirmation);
                return true;
            }

            return false; // Return false if no confirmation exists or not confirmed
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Handle parsing error, possibly by logging or notifying the caller
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if a general error occurs
        }
    }


    
    public boolean stopDonation(Long recipientId, Long loggedInUserId) {
        try {
            Confirmation confirmation = confirmationRepository.findByRecipientIdAndLoggedInUserId(recipientId, loggedInUserId);
            if (confirmation != null && confirmation.isStarted()) {
                confirmation.setStoppedAt(LocalDateTime.now());
                confirmationRepository.save(confirmation);
                return true;  // Return true if update is successful
            }
            return false;  // Return false if confirmation not found or donation not started
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Return false if an error occurs
        }
    }

    public Confirmation getConfirmationByUserIds(Long recipientId, Long loggedInUserId) {
        // Logic to retrieve confirmation based on recipientId and loggedInUserId
        return confirmationRepository.findByRecipientIdAndLoggedInUserId(recipientId, loggedInUserId);
    }
    
    public Confirmation getConfirmationByRecipientIdAndLoggedInUserId(Long recipientId, Long loggedInUserId) {
        return confirmationRepository.findByRecipientIdAndLoggedInUserId(recipientId, loggedInUserId);
    }

    
    @Transactional
    public Confirmation createNewConfirmation(Long recipientId, Long loggedInUserId, String hospitalName) {
        Confirmation existingConfirmation = confirmationRepository.findByRecipientIdAndLoggedInUserId(recipientId, loggedInUserId);

        if (existingConfirmation != null && existingConfirmation.isConfirmed()) {
            System.out.println("Confirmation already exists and is confirmed for recipientId: " + recipientId + 
                               " and loggedInUserId: " + loggedInUserId);
            return existingConfirmation;
        }

        Confirmation confirmation = new Confirmation();
        confirmation.setRecipientId(recipientId);
        confirmation.setLoggedInUserId(loggedInUserId);
        confirmation.setHospitalName(hospitalName);  // Set hospital name
        confirmation.setConfirmedAt(LocalDateTime.now());
        confirmation.setConfirmed(true);
        confirmationRepository.save(confirmation);

        System.out.println("New confirmation created for recipientId: " + recipientId  + 
                           ", loggedInUserId: " + loggedInUserId + 
                           ", hospitalName: " + hospitalName);
        return confirmation;
    }
	

	public List<Confirmation> getAllConfirmations() {
        return confirmationRepository.findAll();
    }
	
	
	public List<Confirmation> getConfirmationsByLoggedInUserId(Long loggedInUserId) {
        return confirmationRepository.findByLoggedInUserId(loggedInUserId);
    }
	
	  public Confirmation findCompletedConfirmationByUserId(Long loggedInUserId) {
	        return confirmationRepository.findFirstByLoggedInUserIdAndStoppedAtIsNotNull(loggedInUserId);
	    }

	  public Confirmation getConfirmationByRecipientId(Long recipientId) {
	        Confirmation confirmation = confirmationRepository.findTopByRecipientIdOrderByIdDesc(recipientId);
	        // Return only if the confirmation exists and is completed
	        return (confirmation != null && confirmation.isCompleted()) ? confirmation : null;
	    }
	  
	  public List<Confirmation> getAllConfirmations(int page, int pageSize) {
		  PageRequest pageable = PageRequest.of(page, pageSize);
	        return confirmationRepository.findAll(pageable).getContent(); // Assuming you are using Spring Data JPA
	        
	    }

	    public long countAllRequests() {
	        return confirmationRepository.count(); // This assumes count() is defined in your repository
	    }

		
	  
}
