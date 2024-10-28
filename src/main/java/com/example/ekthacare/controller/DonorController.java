package com.example.ekthacare.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ekthacare.entity.SearchRequest;
import com.example.ekthacare.entity.SentEmail;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.SearchRequestRepository;
import com.example.ekthacare.repo.SentEmailRepository;
import com.example.ekthacare.repo.UserRepository;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.EmailService;
import com.example.ekthacare.services.ExcelService;
import com.example.ekthacare.services.OtpService;
import com.example.ekthacare.services.SearchRequestService;
import com.example.ekthacare.services.SmsService;
import com.example.ekthacare.services.UserService;
import com.example.ekthacare.services.UserUploadResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

@Controller
public class DonorController {
	
	 @Autowired
	    private UserRepository userRepository;
	 
	  @Autowired
	    private OtpService otpService;

	    @Autowired
	    private EmailService emailService;
	    
	    @Autowired
	    private UserService userService;
	    
	    @Autowired
	    private SearchRequestService searchRequestRepository;
	    
	    
	    @Autowired
	    private  SentEmailRepository sentEmailRepository;
	    
	
	    @Autowired
	    private SmsService smsService;
	    
	    
	    @Autowired
	    private BloodDonationService bloodDonationService;
	    
	    @Autowired
	    private ExcelService excelService;
	  
	    
	 @GetMapping("/donorlogin")
	    public String showDonorHomePage(Model model) {
	        // Add any necessary attributes to the model
	        return "donorlogin";
	    }
	 
	 @GetMapping("/register")
	    public String showRegistrationForm(Model model) {
	        model.addAttribute("user", new User());
	        return "register"; // This will render register.html
	    }
	 
	 @PostMapping("/register")
	    public String registerDonor(@ModelAttribute User user, Model model) {
		// Check if the mobile number already exists
		    if (userRepository.existsByMobile(user.getMobile())) {
		        model.addAttribute("message", "Mobile number already exists. Please use a different number.");
		        return "register"; // Return to registration page with an error message
		    }
		 // Set isVerified to false
	        user.setVerified(false);
	        // Save donor information to the database
	        userRepository.save(user);

	        // Generate and send OTP to the user's mobile number
	        String otp = otpService.generateOtp(user.getMobile());
	       
	        emailService.sendOtp(user.getEmailid(), otp);
	        String message = "User Admin login OTP is " + otp + " - SMSCNT";
	         smsService.sendJsonSms(user.getMobile(), message);

	        // Store user in session for later use
	        model.addAttribute("mobile", user.getMobile());
	        model.addAttribute("message", "OTP sent to your mobile number. Please verify.");

	        return "otp"; // Redirect to OTP verification page
	    }

	   
	   
	 @PostMapping("/login")
	    public String login(@RequestParam String mobile, Model model) {
	        User user = userRepository.findByMobile(mobile);
	        if (user == null) {
	            model.addAttribute("message", "User not found");
	            return "loginerror";
	        }

	        // Generate OTP
	        String otp = otpService.generateOtp(mobile);

	        // Send OTP via email
	        emailService.sendOtp(user.getEmailid(), otp);
	        
	        // Send OTP via SMS using SMSCountry API
	         String message = "User Admin login OTP is " + otp + " - SMSCNT";
	         smsService.sendJsonSms(mobile, message);
	        model.addAttribute("mobile", mobile);
	        return "otp";
	    } 



     
	   
	 @PostMapping("/validateOtp")
	 public String validateOtp(@RequestParam String mobile, @RequestParam String otp, HttpSession session, Model model) {
	     if (otpService.validateOtp(mobile, otp)) {
	         User user = userService.findByMobile(mobile);
	         if (user != null) {
	             session.setAttribute("userId", user.getId());
	             System.out.println("User ID set in session: " + user.getId()); // Debug statement
	             session.setAttribute("userName", user.getDonorname());
	             System.out.println("userName set in session: " + user.getDonorname());
	             return "redirect:/donorhome";
	         } else {
	             model.addAttribute("message", "User not found");
	             return "otp";
	         }
	     } else {
	         model.addAttribute("message", "Invalid OTP");
	         model.addAttribute("mobile", mobile);
	         return "otp";
	     }
	 }


	 @GetMapping("/donorhome")
	 public String showDonorHome(HttpSession session, Model model) {
	     Long userId = (Long) session.getAttribute("userId");
	     System.out.println("Retrieved userId from session: " + userId); // Debug statement
	     if (userId != null) {
	         User user = userService.findById(userId);
	         System.out.println("Retrieved user from database: " + user);
	         if (user != null) {
	             model.addAttribute("user", user);
	             model.addAttribute("userId", userId);
	             return "donorhome";
	         } else {
	             return "redirect:/logout";
	         }
	     }
	     return "redirect:/logout";
	 }

	    
	    @GetMapping("/viewprofile")
	    public String viewProfile(Model model, HttpSession session) {
	        Long userId = (Long) session.getAttribute("userId");
	        System.out.println("Retrieved userId from session1: " + userId);
	        if (userId != null) {
	            User user = userService.findById(userId);
	            if (userId != null) {
	                model.addAttribute("user", user);
	                System.out.println("Retrieved user from database1: " + user);
	                return "viewprofile";
	            } else {
	                model.addAttribute("error", "User not found.");
	                return "home";
	            }
	        } else {
	            model.addAttribute("error", "No user logged in.");
	            return "donorlogin";
	        }
	    }
	    
	    @GetMapping("/logout")
	    public String logout(HttpSession session) {
	        session.invalidate(); // Invalidate the session to log out the user
	        
	        return "redirect:/home"; // Redirect to the login page or home page
	    }
	
	  
	    
	    @GetMapping("/alldonorsdata")
	    public String getAllUsers(Model model, @RequestParam(defaultValue = "0") int page) {
	        int pageSize = 10; // Define the number of users per page

	        // Fetch the paginated list of users
	        List<User> users = userService.getAllUsers(page, pageSize);
	        long totalUsers = userService.countAllRequests(); // Count the total number of users
	        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

	        // Add necessary attributes to the model
	        model.addAttribute("users", users);           // List of users on current page
	        model.addAttribute("currentPage", page);      // Current page number
	        model.addAttribute("totalPages", totalPages); // Total number of pages
	        model.addAttribute("pageSize", pageSize);     // Page size

	        return "alldonorsdata"; // The Thymeleaf template for displaying users
	    }
	    
	    
	    	    
	    @GetMapping("/searchforblood")
	    public String searchForBlood(
	            @RequestParam(value = "bloodgroup", required = false) String bloodgroup,
	            @RequestParam(value = "city", required = false) String city,
	            @RequestParam(value = "state", required = false) String state,
	            Model model, HttpSession session) {

	        // Retrieve userId from session
	        Long userId = (Long) session.getAttribute("userId");
	        System.out.println("Retrieved userId from session: " + userId);

	        User loggedInUser = null;
	        if (userId != null) {
	            loggedInUser = userService.findById(userId);
	            if (loggedInUser != null) {
	                System.out.println("User Details: " + loggedInUser);
	                model.addAttribute("user", loggedInUser);	            

	        // Perform the search
	        List<User> results = List.of();
	        if (bloodgroup != null && city != null) {
	            results = userService.findByBloodgroupAndCityAndState(bloodgroup, city, state);

	            // Get all recipient IDs
	            List<Long> recipientIds = results.stream()
	                    .map(User::getId)
	                    .collect(Collectors.toList());

	            // Fetch last donation dates in one go
	            Map<Long, LocalDateTime> lastDonationDates = bloodDonationService.getLastDonationDatesByRecipientIds(recipientIds);

	            // Filter users based on last donation dates
	            results = results.stream()
	                    .filter(user -> {
	                        LocalDateTime lastDonationDateTime = lastDonationDates.get(user.getId());
	                        // Keep only those with no last donation date or where the date part is null
	                        return lastDonationDateTime == null; // Adjust this logic as needed
	                    })
	                    .collect(Collectors.toList());

	            // Save the search request
	            if (userId != null) {
	                searchRequestRepository.saveSearchRequest(userId, bloodgroup, city, state);
	            }

	            // Send an email to each user in the search results
	            if (loggedInUser != null) {
	                for (User user : results) {
	                    sendEmailToUser(user, loggedInUser);
	                }
	            }
	        }
	        model.addAttribute("results", results);

	        // Add cities and states to model
	        model.addAttribute("cities", userService.getAllCities());
	        model.addAttribute("states", userService.getAllStates());

	        return "searchforblood";
	    } else {
            model.addAttribute("error", "User not found.");
            return "home";
        }
    } else {
        model.addAttribute("error", "No user logged in.");
        return "donorlogin";
    }
}

	    
	    
	    public DonorController(SentEmailRepository sentEmailRepository) {
	        this.sentEmailRepository = sentEmailRepository;
	    }
	    private void sendEmailToUser(User user, User loggedInUser) {
	       
	        String subject = "Blood Search Alert";
	        String confirmationUrl = generateConfirmationUrl(user.getId(), loggedInUser.getId());  // Generate the confirmation URL
	        
	        String message = String.format(
		            "Dear %s,\n\nUser %s is searching for blood group %s in %s, %s. Please contact them if you can help.\n\n" +
		            "Contact Information:\nName: %s\nMobile: %s\nEmail: %s\n\n" +
		            "If you are willing to donate, please confirm by clicking the following link:\n%s\n\nThank you.",
	            user.getDonorname(), loggedInUser.getDonorname(), loggedInUser.getBloodgroup(), 
	            loggedInUser.getCity(), loggedInUser.getState(), loggedInUser.getDonorname(), 
	            loggedInUser.getMobile(), loggedInUser.getEmailid(), confirmationUrl
	        );

	        emailService.sendEmail(user.getEmailid(), subject, message);
	     // Save the email information into the database
	        SentEmail sentEmail = new SentEmail(null, user.getEmailid(), LocalDateTime.now(), confirmationUrl, user.getId(), loggedInUser.getId());
	        sentEmailRepository.save(sentEmail);
	        
	    }
	    
	   // private String generateConfirmationUrl(Long recipientId, Long loggedInUserId) {
	  //      return "http://localhost:8082/confirmRequest?recipientId=" + recipientId + "&loggedInUserId=" + loggedInUserId;
	  //  }  
	    
	    private String generateConfirmationUrl(Long recipientId, Long loggedInUserId) {
	        String token = generateSecureToken(recipientId, loggedInUserId);
	        return "http://localhost:8082/confirmRequest?token=" + token;  // Use token instead of raw IDs
	    }
	    
	 // Method to generate a secure token (could use JWT or encryption)
	    private String generateSecureToken(Long recipientId, Long loggedInUserId) {
	        String data = recipientId + ":" + loggedInUserId;
	        return Base64.getUrlEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
	    }
	   

		
	    @PostMapping("/updateProfile")
	    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
	        // Save change logs here if necessary
	        userService.updateUser(user); // Your service method to update user data
	        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
	        return "redirect:/viewprofile"; // Redirect back to the profile page
	    }

	    
	    @GetMapping("/donorusers/download")
	    public ResponseEntity<byte[]> downloadUserData() throws IOException {
	        List<User> users = userService.getAllUsers(); // Fetch all users from database

	        // Generate CSV
	        StringBuilder csvBuilder = new StringBuilder();
	        csvBuilder.append("ID,Name,Mobile,Email,Date of Birth,Blood Group,Age,Gender,Address,City,State\n");
	        for (User user : users) {
	            csvBuilder.append(user.getId()).append(",")
	                      .append(user.getDonorname()).append(",")
	                      .append(user.getMobile()).append(",")
	                      .append(user.getEmailid()).append(",")
	                      .append(user.getDateofbirth()).append(",")
	                      .append(user.getBloodgroup()).append(",")
	                      .append(user.getAge()).append(",")
	                      .append(user.getGender()).append(",")
	                      .append(user.getAddress()).append(",")
	                      .append(user.getCity()).append(",")
	                      .append(user.getState()).append("\n");
	        }

	        byte[] csvData = csvBuilder.toString().getBytes();

	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "attachment; filename=donorusers.csv");
	        headers.add("Content-Type", "text/csv");

	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(csvData);
	    }
	    
	    
	    // Serve the upload form
	    @GetMapping("/uploadexcel")
	    public String showUploadForm() {
	        return "uploadexcel";
	    }
	    
	    
	 
	 // Handle file upload
	    @PostMapping("/upload")
	    public String uploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
	        try {
	            UserUploadResult result = excelService.parseExcelFile(file);
	            excelService.saveUsersFromFile(file); // Save valid users

	            // Prepare messages
	            StringBuilder duplicateMessage = new StringBuilder();
	            if (!result.getDuplicateUsers().isEmpty()) {
	                duplicateMessage.append("Duplicate users detected: ");
	                for (User duplicate : result.getDuplicateUsers()) {
	                    duplicateMessage.append(duplicate.getMobile()).append(", ");
	                }
	                duplicateMessage.setLength(duplicateMessage.length() - 2); // Remove last comma
	                redirectAttributes.addFlashAttribute("duplicateMessage", duplicateMessage.toString());
	            } else {
	                redirectAttributes.addFlashAttribute("message", "File uploaded and valid data saved successfully!");
	            }

	            // Store valid users in the redirect attributes for display
	            redirectAttributes.addFlashAttribute("users", result.getValidUsers());
	            
	        } catch (IOException e) {
	            redirectAttributes.addFlashAttribute("message", "Failed to upload file: " + e.getMessage());
	        }
	        
	        // Redirect to the GET method to refresh the page
	        return "redirect:/upload"; // Redirect to the upload page
	    }

	    // Add a GET method to handle the upload page
	    @GetMapping("/upload")
	    public String showUploadPage(Model model) {
	        // Clear the model to ensure no previous data is displayed
	        model.addAttribute("message", model.containsAttribute("message") ? model.getAttribute("message") : null);
	        model.addAttribute("duplicateMessage", model.containsAttribute("duplicateMessage") ? model.getAttribute("duplicateMessage") : null);
	        model.addAttribute("users", model.containsAttribute("users") ? model.getAttribute("users") : new ArrayList<>()); // Clear user list

	        return "uploadexcel"; // Return the upload page
	    }


	    	

	    
	  }

	   	

