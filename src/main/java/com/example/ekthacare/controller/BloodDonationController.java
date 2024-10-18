package com.example.ekthacare.controller;

import com.example.ekthacare.entity.BloodDonation;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BloodDonationController {

    @Autowired
    private BloodDonationService bloodDonationService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/mydonations")
    public String getDonations(HttpSession session, Model model) {
    	Long userId = (Long) session.getAttribute("userId");
        System.out.println("Retrieved userId from session: " + userId);
      
        
        User loggedInUserId = null;
        if (userId != null) {
        	loggedInUserId = userService.findById(userId);
        	  System.out.println("Retrieved loggedInUserId from session: " + loggedInUserId); // Debug statement
            if (loggedInUserId != null) {          
               // Fetch the donations for the logged-in user
            	 List<BloodDonation> donations = bloodDonationService.getDonationsByRecipientId(userId);
                 model.addAttribute("donations", donations);
                 return "mydonations"; // The name of your Thymeleaf template
            }
        // Redirect to login if loggedInUserId is not found
        return "mydonations";
    }
		return "redirect:/donorhome";

    }
    
    
    @GetMapping("/alldonationlist")
    public String getAllDonations(HttpSession session, Model model) {
        // Retrieve the list of all donations
        List<BloodDonation> donations = bloodDonationService.getAllDonations();
        model.addAttribute("donations", donations); // Add donations to the model

        return "alldonationlist"; // The name of your Thymeleaf template
    }
}
