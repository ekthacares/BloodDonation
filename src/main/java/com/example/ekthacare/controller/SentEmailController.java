package com.example.ekthacare.controller;


import com.example.ekthacare.entity.SentEmail;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.services.SentEmailService;
import com.example.ekthacare.services.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SentEmailController {
	
	 @Autowired
    private final SentEmailService sentEmailService;
	 
	 @Autowired
	    private UserService userService;
   
    public SentEmailController(SentEmailService sentEmailService) {
        this.sentEmailService = sentEmailService;
    }

    
    
    @GetMapping("/sentEmails")
    public String showSentEmails(HttpSession session, Model model) {
        // Retrieve the userId from the session
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("sentEmails userId from session: " + userId);

        if (userId != null) {
            // Find the logged-in user using the userId from the session
            User loggedInUser = userService.findById(userId);
            System.out.println("loggedInUser userId from session: " + userId);
            
            if (loggedInUser != null) {
                // Retrieve all sent emails where the logged-in user is the recipient
                List<SentEmail> sentEmails = sentEmailService.getSentEmailsByRecipientId(userId);
                System.out.println("Number of sent emails found for recipient: " + sentEmails.size());

                // Check if the sentEmails list is empty and add the appropriate message or data to the model
                if (sentEmails.isEmpty()) {
                    model.addAttribute("message", "No sent emails available for this user as a recipient.");
                } else {
                    model.addAttribute("sentEmails", sentEmails); // Add the sent emails to the model
                }

                // Add the logged-in user to the model
                model.addAttribute("user", loggedInUser);

                return "sentEmails"; // Thymeleaf template name for sent emails
            } else {
                // Handle case where the user is not found
                model.addAttribute("message", "User not found.");
            }
        } else {
            // Handle case where userId is null
            model.addAttribute("message", "User is not logged in.");
        }

        return "sentEmails"; // Return to the same template to show the message
    }


}
