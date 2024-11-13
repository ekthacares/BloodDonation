package com.example.ekthacare.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ekthacare.entity.Confirmation;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.ConfirmationService;
import com.example.ekthacare.services.OtpVerificationService;

@Controller
public class VerificationController {
	
	@Autowired
    private BloodDonationService bloodDonationService;
    
    @Autowired
    private OtpVerificationService otpVerificationService;
    
    @Autowired
    private ConfirmationService confirmationService;
	
         
    @PostMapping("/verifyOtp")
    public String verifyOtp(@RequestParam Long recipientId, @RequestParam Long loggedInUserId, @RequestParam String otp, Model model, RedirectAttributes redirectAttributes) {
        // Validate OTP using the service
        if (otpVerificationService.validateOtp(recipientId, loggedInUserId, otp)) {
            System.out.println("OTP validated successfully, processing confirmation.");

            // Check if confirmation already exists
            Confirmation existingConfirmation = confirmationService.getConfirmationByRecipientId(recipientId);

            if (existingConfirmation != null && !existingConfirmation.getLoggedInUserId().equals(loggedInUserId)) {
                // If another user has already donated to this recipient, show a message
                redirectAttributes.addFlashAttribute("message", "You have already donated to the Registered UserID: " + existingConfirmation.getLoggedInUserId());
                return "redirect:/donationTracking";  // Redirect to donation tracking page
            }

            // No existing confirmation or valid for the logged-in user, create a new confirmation
            Confirmation newConfirmation = confirmationService.createNewConfirmation(recipientId, loggedInUserId);
            redirectAttributes.addFlashAttribute("confirmation", newConfirmation);

            // Update the last donation date
            bloodDonationService.updateLastDonationDate(loggedInUserId, recipientId, LocalDateTime.now());

            // Redirect to the donation tracking page
            return "redirect:/donationTracking";
        } else {
            // If OTP is invalid, display error and stay on the OTP verification page
            model.addAttribute("error", "Invalid OTP. Please try again.");
            model.addAttribute("recipientId", recipientId);  // Retain recipientId and loggedInUserId to allow retry
            model.addAttribute("loggedInUserId", loggedInUserId);
            return "otpVerificationforConfirmurl";  // Return to OTP verification page
        }
    }
    
    @GetMapping("/donationTracking")
    public String showDonationTracking(Model model) {
        // Your logic here
        return "donationTracking";  // This will look for donationTracking.html or donationTracking.jsp
    }
}
