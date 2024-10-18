package com.example.ekthacare.controller;


import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ekthacare.entity.BloodRequest;
import com.example.ekthacare.entity.SearchRequest;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.services.BloodRequestService;
import com.example.ekthacare.services.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BloodRequestController {
    @Autowired
    private BloodRequestService bloodRequestService;
    
 
    
    @Autowired
    private UserService userService;

    @GetMapping("/requestforblood")
    public String showRequestForm(Model model) {
        model.addAttribute("bloodRequest", new BloodRequest());
        return "requestforblood";
    }
    
    

    @PostMapping("/requestforblood")
    public String submitRequest(BloodRequest bloodRequest, Model model) {
        bloodRequestService.saveBloodRequest(bloodRequest);
        model.addAttribute("message", "Blood request submitted successfully!");
        model.addAttribute("bloodRequest", new BloodRequest()); // Reset the form
        return "requestforblood";
    }
    
    

}
  
