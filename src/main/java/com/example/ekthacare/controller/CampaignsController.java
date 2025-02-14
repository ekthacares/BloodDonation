package com.example.ekthacare.controller;

import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.User1Repository;
import com.example.ekthacare.repo.UserRepository;
import com.example.ekthacare.services.CampaignsService;
import com.example.ekthacare.services.User1Service;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CampaignsController {

    private final CampaignsService campaignsService;
    private final UserRepository userRepository;

    public CampaignsController(CampaignsService campaignsService, UserRepository userRepository) {
        this.campaignsService = campaignsService;
        this.userRepository = userRepository;
    }
    
    @Autowired
    private User1Service user1Service;
    
	/*
	 * @GetMapping("/campaign") public String showCampaignHomePage(Model model) { //
	 * Fetch the count of users who have valid FCM tokens long usersWithTokensCount
	 * = userRepository.findAll().stream() .filter(user -> user.getFcmToken() !=
	 * null && !user.getFcmToken().isEmpty()) .count();
	 * 
	 * model.addAttribute("usersWithTokensCount", usersWithTokensCount); return
	 * "campaign"; }
	 */
    @GetMapping("/campaign")
        public String showCampaignHomePage(HttpSession session, Model model) {
	     Long userId = (Long) session.getAttribute("userId");
	     System.out.println("Retrieved userId from session: " + userId); // Debug statement
	     if (userId != null) {
	         User1 user1 = user1Service.findById(userId);
	         System.out.println("Retrieved user from database: " + user1);
	         long usersWithTokensCount = userRepository.findAll().stream()
	                 .filter(user -> user.getFcmToken() != null && !user.getFcmToken().isEmpty())
	                 .count();

	         model.addAttribute("usersWithTokensCount", usersWithTokensCount);	         
	         if (user1 != null) {
	             model.addAttribute("user", user1);
	             model.addAttribute("userId", userId);
	             return "campaign";
	         } else {
	             return "redirect:/adminlogin";
	         }
	     }
	     return "redirect:/adminlogin";
	 }
    
    @GetMapping("/create")
    public String showCreateCampaignForm(HttpSession session, Model model) {
    	 Long userId = (Long) session.getAttribute("userId");
	     System.out.println("Retrieved userId from session: " + userId); // Debug statement
	     if (userId != null) {
	         User1 user1 = user1Service.findById(userId);
	         System.out.println("Retrieved user from database: " + user1);
        // Fetch the count of users with FCM tokens
        long usersWithTokensCount = userRepository.findAll().stream()
                .filter(user -> user.getFcmToken() != null && !user.getFcmToken().isEmpty())
                .count();

        model.addAttribute("campaigns", campaignsService.getAllCampaigns());
        model.addAttribute("usersWithTokensCount", usersWithTokensCount);
        if (user1 != null) {
            model.addAttribute("user", user1);
            model.addAttribute("userId", userId);
            return "campaign";
        } else {
            return "redirect:/adminlogin";
        }
    }
    return "redirect:/adminlogin";
}
    

    @PostMapping("/save")
    public String createCampaign(@RequestParam String title, 
                                 @RequestParam String message,
                                 @RequestParam String sendTo,
                                 @RequestParam(required = false) String mobileNumbers) {
        campaignsService.createCampaign(title, message, sendTo, mobileNumbers);
        return "redirect:/create";
    }
}