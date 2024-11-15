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

            Confirmation existingConfirmation = confirmationService.getConfirmationByRecipientId(recipientId);

            if (existingConfirmation != null && !existingConfirmation.getLoggedInUserId().equals(loggedInUserId)) {
                redirectAttributes.addFlashAttribute("message", "You have already donated to the Registered UserID: " + existingConfirmation.getLoggedInUserId());
                return "redirect:/donationTracking";
            }

            // Pass hospitalName to the confirmation service if applicable
            Confirmation newConfirmation = confirmationService.createNewConfirmation(recipientId, loggedInUserId, hospitalName);
            System.out.println("Hospital Name in createNewConfirmation: " + hospitalName);  // Print hospital name here too
            redirectAttributes.addFlashAttribute("confirmation", newConfirmation);

            bloodDonationService.updateLastDonationDate(loggedInUserId, recipientId, LocalDateTime.now(), hospitalName);  // Pass hospital name here as well
            System.out.println("Hospital Name in updateLastDonationDate: " + hospitalName);  // Print hospital name here too

            return "redirect:/donationTracking";
        } else {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            model.addAttribute("recipientId", recipientId);
            model.addAttribute("loggedInUserId", loggedInUserId);
            return "otpVerificationforConfirmurl";
        }
    }

    
    @GetMapping("/donationTracking")
    public String showDonationTracking(Model model) {
        // Your logic here
        return "donationTracking";  // This will look for donationTracking.html or donationTracking.jsp
    }
}
