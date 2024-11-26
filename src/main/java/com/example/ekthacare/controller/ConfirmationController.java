package com.example.ekthacare.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ekthacare.entity.Confirmation;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.ConfirmationService;
import com.example.ekthacare.services.EmailService;
import com.example.ekthacare.services.OtpVerificationService;
import com.example.ekthacare.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ConfirmationController {

    @Autowired
    private ConfirmationService confirmationService;
    
    @Autowired
    private OtpVerificationService otpVerificationService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;
       
    @GetMapping("/confirmRequest")
    public String confirmRequest(
            @RequestParam String token, 
            @RequestParam(required = false) String hospitalName, 
            Model model) {

        try {
            // Decode the token to retrieve loggedInUserId and recipientId
            String decodedData = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decodedData.split(":");

            if (parts.length != 2) {
                model.addAttribute("message", "Invalid confirmation link.");
                System.out.println("Invalid token received: " + token);
                return "errorPage";
            }

            Long recipientId = Long.parseLong(parts[1]);
            Long loggedInUserId = Long.parseLong(parts[0]);

            System.out.println("Logged-In User ID: " + loggedInUserId);

            // Retrieve all existing confirmations for the logged-in user
            List<Confirmation> existingConfirmations = confirmationService.getConfirmationsByLoggedInUserId(loggedInUserId);

            if (existingConfirmations != null && !existingConfirmations.isEmpty()) {
                // If there are existing confirmations, check them
                for (Confirmation existingConfirmation : existingConfirmations) {
                    System.out.println("Existing confirmation found: " + existingConfirmation);

                    // Check if the recipientId in the confirmation matches the current recipientId
                    if (!existingConfirmation.getRecipientId().equals(recipientId)) {
                        // If recipientId is different, show a message
                        System.out.println("Mismatch in recipientId. User has already donated to another recipient.");
                        model.addAttribute("message", "You have already donated to the Registered UserID: " + existingConfirmation.getRecipientId());
                        return "donationTracking";
                    }

                    // Check if this confirmation is already completed
                    if (existingConfirmation.isCompleted()) {
                        System.out.println("Completed confirmation found for logged-in user ID: " + loggedInUserId);
                        model.addAttribute("confirmation", existingConfirmation);
                        return "donationTracking";
                    }
                }

                // If no completed confirmations are found, proceed as normal
                System.out.println("No completed confirmation found for logged-in user ID: " + loggedInUserId);
            } else {
                System.out.println("No existing confirmations found for loggedInUserId: " + loggedInUserId);
            }

            // Ensure that the hospital name is set in the confirmation creation
            if (hospitalName == null || hospitalName.isEmpty()) {
                System.out.println("Hospital name is missing, setting default value.");
                hospitalName = "Unknown Hospital"; // Default if no hospital name provided
            }
            System.out.println("Hospital Name: " + hospitalName);

            // Generate OTP for the logged-in user
            String otp = otpVerificationService.generateAndSaveOtp(recipientId, loggedInUserId);

            // Find the recipient user based on recipientId
            User recipient = userService.findById(loggedInUserId);
            if (recipient != null) {
                // Send OTP to recipient's email
                String otpMessage = "Your OTP for confirming the blood donation request is: " + otp;
                try {
                    emailService.sendEmail(recipient.getEmailid(), "Blood Donation OTP", otpMessage);
                    System.out.println("OTP sent to recipient: " + recipient.getEmailid());
                } catch (Exception e) {
                    System.out.println("Error sending OTP to recipient: " + recipient.getEmailid());
                    e.printStackTrace();
                    model.addAttribute("message", "Failed to send OTP. Please try again later.");
                    return "errorPage";  // Handle error in sending OTP
                }
            } else {
                System.out.println("Recipient not found for recipientId: " + loggedInUserId);
                model.addAttribute("message", "Recipient not found.");
                return "errorPage";  // If the recipient does not exist, show error page
            }

            // Add recipient, logged-in user, and hospital name info to the model
            model.addAttribute("recipientId", recipientId);
            model.addAttribute("loggedInUserId", loggedInUserId);
            model.addAttribute("hospitalName", hospitalName);  // Add hospital name to the model (if present)

            System.out.println("OTP generated and sent for recipient ID: " + recipientId + " by user ID: " + loggedInUserId);
            return "otpVerificationforConfirmurl";  // Redirect to OTP input page for user to enter OTP
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "An error occurred while processing the confirmation request.");
            return "errorPage";  // Handle generic errors
        }
    }


    @PostMapping("/startDonation")
    public String startDonation(
            @RequestParam Long recipientId, 
            @RequestParam Long loggedInUserId, 
            Model model) {

        confirmationService.startDonation(recipientId, loggedInUserId);

        model.addAttribute("confirmation", confirmationService.getConfirmation(recipientId, loggedInUserId));
        return "donationTracking";  // Direct to the tracking page after starting donation
    }
    
    

    @PostMapping("/stopDonation")
    public String stopDonation(
            @RequestParam Long recipientId, 
            @RequestParam Long loggedInUserId, 
            Model model) {

        confirmationService.stopDonation(recipientId, loggedInUserId);

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
                // Retrieve confirmations for the logged-in user by recipientId
                List<Confirmation> confirmations = confirmationService.getConfirmationsByRecipientId(userId);
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
    public String getConfirmations(Model model, 
                                   @RequestParam(defaultValue = "0") int page) {
        int pageSize = 10; // Define the number of confirmations per page

        // Fetch paginated list of confirmations
        List<Confirmation> confirmations = confirmationService.getAllConfirmations(page, pageSize);
        long totalRequests = confirmationService.countAllRequests();
        int totalPages = (int) Math.ceil((double) totalRequests / pageSize);

        // Add attributes to the model
        model.addAttribute("confirmations", confirmations);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages); // Use totalPages variable directly
        model.addAttribute("pageSize", pageSize);

        return "allconfirmations"; // The Thymeleaf template name
    }
}
