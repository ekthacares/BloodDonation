package com.example.ekthacare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.ekthacare.services.CampaignsService;

@Controller
public class CampaignsController {

    private final CampaignsService campaignsService;

    public CampaignsController(CampaignsService campaignsService) {
        this.campaignsService = campaignsService;
    }
    
    @GetMapping("/campaign")
    public String showcampaignHomePage(Model model) {
        // Add any necessary attributes to the model
        return "campaign";
    }

    @GetMapping("/create")
    public String showCreateCampaignForm(Model model) {
        model.addAttribute("campaigns", campaignsService.getAllCampaigns());
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
