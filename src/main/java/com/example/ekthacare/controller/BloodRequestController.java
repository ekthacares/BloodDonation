package com.example.ekthacare.controller;


import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

import java.io.IOException;
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
    
    @GetMapping("/viewbloodrequestdatadownload")
    public ResponseEntity<byte[]> downloadBloodRequests() throws IOException {
        List<BloodRequest> bloodRequests = bloodRequestService.getAllBloodRequests(); // Fetch your blood requests

        // Generate CSV data
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Name,Blood Group,City,State,Mobile\n");
        for (BloodRequest request : bloodRequests) {
            csvBuilder.append(request.getId()).append(",")
                      .append(request.getName()).append(",")
                      .append(request.getBloodgroup()).append(",")
                      .append(request.getCity()).append(",")
                      .append(request.getState()).append(",")                      
                      .append(request.getMobile()).append("\n");
        }

        byte[] csvData = csvBuilder.toString().getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=blood_requests.csv");
        headers.add("Content-Type", "text/csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

}
  
