package com.example.ekthacare.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ekthacare.entity.Confirmation;
import com.example.ekthacare.entity.SentEmail;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.ConfirmationService;
import com.example.ekthacare.services.SentEmailService;
import com.example.ekthacare.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ConfirmationController {

    @Autowired
    private ConfirmationService confirmationService;
    
    private final SentEmailService sentEmailService = null;
    
    @Autowired
    private BloodDonationService bloodDonationService;

    
    @Autowired
    private UserService userService;
       
    @GetMapping("/confirmRequest")
    public String confirmRequest(
            @RequestParam String token, 
            Model model) {

        // Decode the token to retrieve recipientId and loggedInUserId
        String decodedData = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
        String[] parts = decodedData.split(":");

        if (parts.length != 2) {
            model.addAttribute("message", "Invalid confirmation link.");
            System.out.println("Invalid token received: " + token);
            return "errorPage";
        }

        Long recipientId = Long.parseLong(parts[0]);
        Long loggedInUserId = Long.parseLong(parts[1]);

        System.out.println("Recipient ID: " + recipientId + ", Logged-In User ID: " + loggedInUserId);

        // Retrieve any existing confirmation for the recipient
        Confirmation existingConfirmation = confirmationService.getConfirmationByRecipientId(recipientId);

        if (existingConfirmation != null) {
            // If another user has an existing confirmation, show message and do not allow new confirmation
            if (!existingConfirmation.getLoggedInUserId().equals(loggedInUserId)) {
                model.addAttribute("message", "A completed donation process already exists for this recipient by another user.");
                System.out.println("donation found for recipient ID: " + recipientId + 
                                   " by another user. Current User ID: " + loggedInUserId + " cannot confirm.");
                return "donationTracking";
            }
            
            // If the existing confirmation belongs to the logged-in user and is completed, show a completed message
            if (existingConfirmation.isCompleted()) {
                model.addAttribute("confirmation", existingConfirmation);
              //  model.addAttribute("message", "A completed donation process already exists for this recipient.");
                System.out.println("Completed confirmation found for recipient ID: " + recipientId);
                return "donationTracking";
            }
        }

        // No existing conflicting confirmation, proceed with creating a new confirmation for this user
        Confirmation newConfirmation = confirmationService.createNewConfirmation(recipientId, loggedInUserId);
        model.addAttribute("confirmation", newConfirmation);
        System.out.println("New confirmation created for recipient ID: " + recipientId + " by user ID: " + loggedInUserId);
        	
        bloodDonationService.updateLastDonationDate(loggedInUserId, recipientId, LocalDateTime.now());
        System.out.println("Updated last donation date for user ID: " + loggedInUserId + " for recipient ID: " + recipientId);

        return "donationTracking";
    }




    @PostMapping("/startDonation")
    public String startDonation(
            @RequestParam Long recipientId, 
            @RequestParam Long loggedInUserId, 
            Model model) {

        boolean isStarted = confirmationService.startDonation(recipientId, loggedInUserId);

        model.addAttribute("confirmation", confirmationService.getConfirmation(recipientId, loggedInUserId));
        return "donationTracking";  // Direct to the tracking page after starting donation
    }

    @PostMapping("/stopDonation")
    public String stopDonation(
            @RequestParam Long recipientId, 
            @RequestParam Long loggedInUserId, 
            Model model) {

        boolean isStopped = confirmationService.stopDonation(recipientId, loggedInUserId);

        model.addAttribute("confirmation", confirmationService.getConfirmation(recipientId, loggedInUserId));
        return "donationTracking";  // Direct to the tracking page after stopping donation
    }
    
    @GetMapping("/donortracking")
    public String getDonationTrackingForLoggedInUser(HttpSession session, Model model) {
        // Retrieve the userId from the session
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("donor tracking userId from session: " + userId);

        if (userId != null) {
            // Find the logged-in user using the userId from the session
            User loggedInUser = userService.findById(userId);
            System.out.println("loggedInUser userId from session: " + userId);
            
            if (loggedInUser != null) {
                // Retrieve confirmations for the logged-in user
                List<Confirmation> confirmations = confirmationService.getConfirmationsByLoggedInUserId(userId);
                if (confirmations == null || confirmations.isEmpty()) {
                    model.addAttribute("message", "No donation tracking information available.");
                } else {
                    model.addAttribute("confirmations", confirmations);  // Add the user's confirmations to the model
                }

                // Add the logged-in user to the model
                model.addAttribute("user", loggedInUser);

                return "donortracking";  // Return the Thymeleaf template that shows the donation tracking list
            } else {
                // Handle case where the user was not found
                model.addAttribute("message", "User not found.");
                return "errorPage";  // Redirect to an error page
            }
        } else {
            // Handle the case when the userId is not in the session
            model.addAttribute("message", "User not logged in.");
            return "errorPage";  // Redirect to an error page
        }
    }

    @GetMapping("allconfirmations")
    public String viewAllConfirmations(Model model) {
        List<Confirmation> confirmations = confirmationService.getAllConfirmations();
        model.addAttribute("confirmations", confirmations);
        return "allconfirmations"; // Path to the Thymeleaf template
    }

}
