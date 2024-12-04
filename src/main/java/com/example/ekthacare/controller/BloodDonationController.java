package com.example.ekthacare.controller;

import com.example.ekthacare.entity.BloodDonation;
import com.example.ekthacare.entity.Confirmation;
import com.example.ekthacare.entity.SearchRequest;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.ConfirmationService;
import com.example.ekthacare.services.SearchRequestService;
import com.example.ekthacare.services.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BloodDonationController {

    @Autowired
    private BloodDonationService bloodDonationService;
    
    @Autowired
    private SearchRequestService searchRequestService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/mydonations")
    public String getDonations(HttpSession session, Model model) {
    	
    	if (session.getAttribute("userId") == null) {
            // User is not logged in, redirect to login page
            return "redirect:/donorlogin"; // Adjust the URL to your login page
        }

        // Retrieve the logged-in user's ID from the session
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("Retrieved userId from session: " + userId);

        if (userId != null) {
            // Retrieve the logged-in user's details
            User loggedInUser = userService.findById(userId);
            System.out.println("Retrieved loggedInUser from session: " + loggedInUser); // Debug statement

            if (loggedInUser != null) {
                // Fetch all donations or confirmations made by this user (as a list)
              //  List<Confirmation> donations = confirmationService.findDonationsByUserId(userId);
                List<BloodDonation> donations = bloodDonationService.getAllDonations(userId);
                
                if (donations.isEmpty()) {
                    // Add a message if no donations are found
                    model.addAttribute("message", "You have not donated to anyone");
                } else {
                    // Add the list of donations to the model
                    model.addAttribute("donations", donations);
                }

                // Return the view name
                return "mydonations"; // The name of your Thymeleaf template
            }
        }

        // Redirect to donor home if no valid user or donations found
        return "redirect:/donorhome";
    }

    @PostMapping("/addDonation")
    public String addDonation(@RequestParam(value = "lastDonationDate", required = false) 
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDonationDate,
                              @RequestParam(value = "hospitalName", required = false) String hospitalName,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {

        // Retrieve userId from session
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("Retrieved userId from session: " + userId);

        if (userId == null) {
            // Handle missing userId
            redirectAttributes.addFlashAttribute("message", "You must be logged in to add a donation.");
            return "redirect:/login"; // Redirect to login or an appropriate page
        }

        // Retrieve logged-in user details
        User loggedInUser = userService.findById(userId);
        System.out.println("Retrieved loggedInUser from session: " + loggedInUser);

        if (loggedInUser == null) {
            // Handle case where user is not found in the database
            redirectAttributes.addFlashAttribute("message", "User not found. Please try again.");
            return "redirect:/mydonations"; // Redirect back to the donations page
        }

        if (lastDonationDate == null || hospitalName == null || hospitalName.isEmpty()) {
            // Handle missing parameters
            redirectAttributes.addFlashAttribute("message", "Please provide all required details to add a donation.");
            return "redirect:/mydonations"; // Redirect to the donations form
        }

        // Save the new donation record
        bloodDonationService.createNewDonationRecord(userId, null , lastDonationDate, hospitalName);
        redirectAttributes.addFlashAttribute("message", "Last donation date added successfully!");

        return "redirect:/mydonations"; // Redirect to the donations list page
    }

    
    
    @GetMapping("/totaldonationlist")
    public String getAllDonations(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page) {
                        int pageSize = 10;

                // Fetch the donations based on the current page and page size
                List<BloodDonation> donations = bloodDonationService.getAllDonations(page, pageSize);

                // Calculate the total number of donations
                long totalDonations = bloodDonationService.countAllDonations();
                int totalPages = (int) Math.ceil((double) totalDonations / pageSize);

                // Add attributes to the model to be used in the Thymeleaf template
                model.addAttribute("donations", donations);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("pageSize", pageSize);
               

                // Return the Thymeleaf template name
                return "totaldonationlist";
            } 
    
    @GetMapping("/totaldonationlist/download")
    public ResponseEntity<byte[]> downloadDonationsData() {
        List<BloodDonation> donations = bloodDonationService.getAllDonations();

        // Build CSV content
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,DonatedTo,DonatedBy,LastDonationDate\n");
        for (BloodDonation donation : donations) {
            csvBuilder.append(donation.getId()).append(",")
                      .append(donation.getUserId()).append(",")
                      .append(donation.getRecipientId()).append(",")
                      .append(donation.getLastDonationDate()).append("\n");
        }

        // Convert CSV to byte array
        byte[] csvData = csvBuilder.toString().getBytes();

        // Set headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=alldonationslistofregistered users.csv");

        return ResponseEntity.ok()
                .headers(headers)
               
                .body(csvData);
    }
    
    @GetMapping("/allsearchrequestdata/download")
    public ResponseEntity<byte[]> downloadSearchRequestsData() {
        List<SearchRequest> searchRequests = searchRequestService.getAllSearchRequests();

        // Building CSV content
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("User ID,Blood Group,City,State,TimeStamp\n");
        for (SearchRequest request : searchRequests) {
            csvBuilder.append(request.getUserId()).append(",")
                      .append(request.getBloodgroup()).append(",")
                      .append(request.getCity()).append(",")
                      .append(request.getState()).append(",")
                      .append(request.getTimestamp()).append("\n");
        }

        // Convert CSV content to byte array
        byte[] csvData = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);

        // Set headers to prompt for download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=search_requests.csv");

        return ResponseEntity.ok()
                .headers(headers)               
                .body(csvData);
    }
}
