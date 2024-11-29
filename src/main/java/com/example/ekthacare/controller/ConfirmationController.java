package com.example.ekthacare.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
import com.example.ekthacare.services.SmsService;
import com.example.ekthacare.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ConfirmationController {

    @Autowired
    private ConfirmationService confirmationService;
    
    @Autowired
    private OtpVerificationService otpVerificationService;
    
    @Autowired
    private BloodDonationService bloodDonationService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SmsService smsService;
       
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

            // Check the last donation date for the logged-in user
            LocalDateTime lastDonationDateTime = bloodDonationService.getLastDonationDateByUserId(loggedInUserId);

            if (lastDonationDateTime != null) {
                // Convert LocalDateTime to LocalDate
                LocalDate lastDonationDate = lastDonationDateTime.toLocalDate();
                LocalDate currentDate = LocalDate.now();

                // Calculate the period
                Period periodSinceLastDonation = Period.between(lastDonationDate, currentDate);
                int monthsSinceLastDonation = periodSinceLastDonation.getMonths();

                if (monthsSinceLastDonation < 3) {
                    model.addAttribute("message", "You are not eligible to donate yet. Your last donation was on " 
                        + lastDonationDate + ". Please wait for at least 3 months.");
                    return "donationTracking";
                }
            } else {
                System.out.println("Donor not found for loggedInUserId: " + loggedInUserId);
                model.addAttribute("message", "Donor information not found.");
                return "errorPage";
            }


            // Retrieve all existing confirmations for the logged-in user
            List<Confirmation> existingConfirmations = confirmationService.getConfirmationsByLoggedInUserId(loggedInUserId);

            if (existingConfirmations != null && !existingConfirmations.isEmpty()) {
                for (Confirmation existingConfirmation : existingConfirmations) {
                    System.out.println("Existing confirmation found: " + existingConfirmation);

                    if (!existingConfirmation.getRecipientId().equals(recipientId)) {
                        System.out.println("Mismatch in recipientId. User has already donated to another recipient.");
                        model.addAttribute("message", "You have already donated to the Registered UserID: " + existingConfirmation.getRecipientId());
                        return "donationTracking";
                    }

                    if (existingConfirmation.isCompleted()) {
                        System.out.println("Completed confirmation found for logged-in user ID: " + loggedInUserId);
                        model.addAttribute("confirmation", existingConfirmation);
                        return "donationTracking";
                    }
                }
            } else {
                System.out.println("No existing confirmations found for loggedInUserId: " + loggedInUserId);
            }

            if (hospitalName == null || hospitalName.isEmpty()) {
                hospitalName = "Unknown Hospital";
            }
            System.out.println("Hospital Name: " + hospitalName);

            // Generate OTP for the logged-in user
            String otp = otpVerificationService.generateAndSaveOtp(recipientId, loggedInUserId);

            // Find the recipient user based on recipientId
            User recipient = userService.findById(loggedInUserId);
            if (recipient != null) {
                String otpMessage = "Your OTP for confirming the blood donation request is: " + otp;

                // Send OTP via email
                try {
                    emailService.sendEmail(recipient.getEmailid(), "Blood Donation OTP", otpMessage);
                    System.out.println("OTP sent to recipient: " + recipient.getEmailid());
                } catch (Exception e) {
                    System.out.println("Error sending OTP to recipient: " + recipient.getEmailid());
                    e.printStackTrace();
                    model.addAttribute("message", "Failed to send OTP. Please try again later.");
                    return "errorPage";
                }

                // Send OTP via SMS
                try {
                    // String message = "Your OTP for confirming the blood donation is " + otp + " - EKTHA PVT LTD";
                    // smsService.sendJsonSms(recipient.getMobile(), message);  // Assuming smsService is implemented
                    System.out.println("OTP sent via SMS to recipient: " + recipient.getMobile());
                } catch (Exception e) {
                    System.out.println("Error sending OTP via SMS to recipient: " + recipient.getMobile());
                    e.printStackTrace();
                    model.addAttribute("message", "Failed to send OTP via SMS. Please try again later.");
                    return "errorPage";
                }
            } else {
                System.out.println("Recipient not found for recipientId: " + recipientId);
                model.addAttribute("message", "Recipient not found.");
                return "errorPage";
            }

            model.addAttribute("recipientId", recipientId);
            model.addAttribute("loggedInUserId", loggedInUserId);
            model.addAttribute("hospitalName", hospitalName);

            System.out.println("OTP generated and sent for recipient ID: " + loggedInUserId);
            return "otpVerificationforConfirmurl";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "An error occurred while processing the confirmation request.");
            return "errorPage";
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
