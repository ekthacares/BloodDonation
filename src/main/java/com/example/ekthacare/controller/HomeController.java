package com.example.ekthacare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String showHomePage(Model model) {
        // Add any necessary attributes to the model
        return "home";
    }
    
    @GetMapping("/contact")
    public String showContactPage(Model model) {
        // Add any necessary attributes to the model
        return "contact";
    }
    
    @GetMapping("/about-us")
    public String showAboutUsPage(Model model) {
        // Add any necessary attributes to the model
        return "about-us";
    }
   }
