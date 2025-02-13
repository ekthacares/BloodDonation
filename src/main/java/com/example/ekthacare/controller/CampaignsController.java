package com.example.ekthacare.controller;

import com.example.ekthacare.repo.UserRepository;
import com.example.ekthacare.services.CampaignsService;
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

    @GetMapping("/campaign")
    public String showCampaignHomePage(Model model) {
        // Fetch the count of users who have valid FCM tokens
        long usersWithTokensCount = userRepository.findAll().stream()
                .filter(user -> user.getFcmToken() != null && !user.getFcmToken().isEmpty())
                .count();

        model.addAttribute("usersWithTokensCount", usersWithTokensCount);
        return "campaign";
    }

    @GetMapping("/create")
    public String showCreateCampaignForm(Model model) {
        // Fetch the count of users with FCM tokens
        long usersWithTokensCount = userRepository.findAll().stream()
                .filter(user -> user.getFcmToken() != null && !user.getFcmToken().isEmpty())
                .count();

        model.addAttribute("campaigns", campaignsService.getAllCampaigns());
        model.addAttribute("usersWithTokensCount", usersWithTokensCount);
        return "campaign";
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
