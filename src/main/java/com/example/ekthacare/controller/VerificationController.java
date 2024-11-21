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
    public String verifyOtp(@RequestParam Long recipientId, 
                            @RequestParam Long loggedInUserId, 
                            @RequestParam String otp, 
                            @RequestParam(required = false) String hospitalName,  // Add hospital name here
                            Model model, 
                            RedirectAttributes redirectAttributes) {
        System.out.println("Hospital Name passed to verifyOtp: " + hospitalName);  // Print hospital name here

        if (otpVerificationService.validateOtp(recipientId, loggedInUserId, otp)) {
            System.out.println("OTP validated successfully, processing confirmation.");

            // Get the existing confirmation based on both recipientId and loggedInUserId
            Confirmation existingConfirmation = confirmationService.getConfirmationByRecipientIdAndLoggedInUserId(recipientId, loggedInUserId);

            // Check if an existing confirmation exists
            if (existingConfirmation != null) {
                System.out.println("User with ID " + loggedInUserId + " has an existing confirmation.");
                System.out.println("Existing Confirmation Details: " + existingConfirmation.toString());  // Prints details of the existing confirmation

                // Check if the loggedInUserId matches the confirmation's loggedInUserId
                if (!existingConfirmation.getLoggedInUserId().equals(loggedInUserId)) {
                    redirectAttributes.addFlashAttribute("message", "You have already donated to the Registered UserID: " + existingConfirmation.getLoggedInUserId());
                    return "redirect:/donationTracking";
                } else {
                    // If loggedInUserId matches, process the donation confirmation
                    redirectAttributes.addFlashAttribute("confirmation", existingConfirmation);
                }
            } else {
                // No existing confirmation found, so create a new one
            	 // Pass hospitalName to the confirmation service if applicable
                Confirmation newConfirmation = confirmationService.createNewConfirmation(recipientId, loggedInUserId, hospitalName);
                System.out.println("New confirmation created for recipientId: " + recipientId + " and loggedInUserId: " + loggedInUserId);

                // Save the new confirmation in the model for further processing
                redirectAttributes.addFlashAttribute("confirmation", newConfirmation);
            }

            // Additional logic to update the donation tracking
            bloodDonationService.updateLastDonationDate(loggedInUserId, recipientId, LocalDateTime.now(), hospitalName);  // Update last donation date
            System.out.println("Hospital Name in updateLastDonationDate: " + hospitalName);  // Print hospital name

            return "redirect:/donationTracking";  // Redirect to donation tracking page
        } else {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            model.addAttribute("recipientId", recipientId);
            model.addAttribute("loggedInUserId", loggedInUserId);
            return "otpVerificationforConfirmurl";  // Redirect to OTP verification page if OTP is invalid
        }

        
    }

    
    @GetMapping("/donationTracking")
    public String showDonationTracking(Model model) {
        // Your logic here
        return "donationTracking";  // This will look for donationTracking.html or donationTracking.jsp
    }
}
