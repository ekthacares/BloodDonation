package com.example.ekthacare.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ekthacare.entity.SentEmail;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.SentEmailRepository;
import com.example.ekthacare.repo.User1Repository;
import com.example.ekthacare.repo.UserRepository;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.EmailService;
import com.example.ekthacare.services.ExcelService;
import com.example.ekthacare.services.OtpService;
import com.example.ekthacare.services.SearchRequestService;
import com.example.ekthacare.services.SmsService;
import com.example.ekthacare.services.User1Service;
import com.example.ekthacare.services.UserService;
import com.example.ekthacare.services.UserUploadResult;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import jakarta.servlet.http.HttpSession;


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
	    private ExcelService excelService;
	  
	    
		/* =======================New user/registration from Donorside============================= */
	    
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
	        user.setIsVerified(true);
	        // Save donor information to the database
	        userRepository.save(user);

	        // Generate and send OTP to the user's mobile number
	        String otp = otpService.generateOtp(user.getMobile());
	       
	     // Send OTP via email
		     String emailMessage = "Your OTP for Regristration  is : <b>" + otp + "</b>. For Mobile Number: <b>" + user.getMobile() + "</b>";
		     //emailService.sendOtp(user.getEmailid(),  "Your OTP Code", emailMessage);
		     emailService.sendHtmlEmail(user.getEmailid(), "Your OTP Code", emailMessage);
		     
		     // Send OTP via SMS using SMSCountry API
			    // String message = "User Admin login OTP is " + otp + " - SMSCNT";
			     String message = "Your Registration OTP is " + otp + " - EKTHA PVT LTD";
			     smsService.sendJsonSms(user.getMobile(), message);
			     
			     
	        // Store user in session for later use
	        model.addAttribute("mobile", user.getMobile());
	        model.addAttribute("message", "OTP sent to your mobile number/ Email and valids for 2minutes");

	        return "otpr"; // Redirect to OTP verification page
	    }
	 	
	 @PostMapping("/validateOtpr")
	 public String validateOtpr(@RequestParam String mobile, @RequestParam String otp, HttpSession session, Model model) {
		 // Check if OTP is valid and not expired
		    if (otpService.isOtpExpired(mobile)) {
		        model.addAttribute("message", "OTP has expired. Please request a new one.");
		        model.addAttribute("mobile", mobile);
		        model.addAttribute("showResendButton", true); // Add flag to display resend button
		        return "otpr"; // Return to OTP page with the resend button
		    }
		 
		 if (otpService.validateOtp(mobile, otp)) {
	         User user = userService.findByMobile(mobile);
	         if (user != null) {
	             // Assuming you want to set `createdBy` after OTP validation
	             user.setCreatedBy(user.getId());  // Set the `createdBy` field to the current user's ID (or other relevant field)
	             
	             // Set createdByType to "donor" since it is creating this donorregistration
	    	     user.setCreatedByType("Self");
	    	     
	    	  // Set the createdAt field to the current time
	    	     user.setCreatedAt(LocalDateTime.now());  // Set the creation timestamp
	    	     
	    	  // Mark the user as verified
	             user.setIsVerified(true); // Assuming `isVerified` is a boolean field in the `User` entity

	             // Save the user with updated `createdBy`
	             userService.save(user); // Assuming save method persists the user entity
	             
	             // Set session attributes
	             session.setAttribute("userId", user.getId());
	             System.out.println("User ID set in session: " + user.getId()); // Debug statement
	             session.setAttribute("userName", user.getDonorname());
	             System.out.println("userName set in session: " + user.getDonorname());

	             // Redirect to the donor home page after successful OTP validation and update
	             return "redirect:/donorhome";
	         } else {
	             model.addAttribute("message", "User not found");
	             return "otpr"; // Return to OTP page if user is not found
	         }
	     } else {
	         model.addAttribute("message", "Invalid OTP");
	         model.addAttribute("mobile", mobile);
	         return "otpr"; // Return to OTP page if OTP is invalid
	     }
	 }
	 
	 @PostMapping("/resendOtpr")
	 public String resendOtpr(@RequestParam String mobile, Model model) {
	     String newOtp = otpService.generateOtp(mobile);

	     // Send new OTP via email
	     User user = userService.findByMobile(mobile);
	     // Send OTP via email
	     String emailMessage = "Your OTP for Regristration  is : <b>" + newOtp + "</b>. For Mobile Number: <b>" + mobile + "</b>";
	     //emailService.sendOtp(user.getEmailid(),  "Your OTP Code", emailMessage);
	     emailService.sendHtmlEmail(user.getEmailid(), "Your OTP Code", emailMessage);
	     
	     // Send OTP via SMS using SMSCountry API
		    // String message = "User Admin login OTP is " + otp + " - SMSCNT";
		     String message = "Your Registration OTP is " + newOtp + " - EKTHA PVT LTD";
		     smsService.sendJsonSms(mobile, message);

	     

	     model.addAttribute("message", "A new OTP has been sent to your mobile.");
	     model.addAttribute("mobile", mobile);
	     return "otpr";
	 }

	 
	 /* =========================Donor registration from Admin============================================ */
	 
	 @GetMapping("/donorregister")
	    public String showDonorRegistrationForm(Model model) {
	        model.addAttribute("user", new User());
	        return "donorregister"; // This will render register.html
	    }	 
	 
	 @PostMapping("/donorregister")
	 public String registerDonorByAdmin(@ModelAttribute User user, Model model, HttpSession session) {
	     // Check if the mobile number already exists
	     if (userRepository.existsByMobile(user.getMobile())) {
	         model.addAttribute("message", "Mobile number already exists. Please use a different number.");
	         return "register"; // Return to registration page with an error message
	     }
	     
	     // Retrieve the admin userId from the session
	     Long userId = (Long) session.getAttribute("userId");

	     // Print the userId to verify
	     System.out.println("Admin userId from session: " + userId);
	     
	     // Ensure userId exists in the session
	     if (userId == null) {
	         model.addAttribute("message", "Admin session expired. Please log in again.");
	         return "adminlogin"; // Redirect to admin login if session is invalid
	     }
	     
	    	     
	     // Set createBy column in the User entity to the admin's userId (userId from session)
	     user.setCreatedBy(userId);  // Set the admin's userId as the creator of the donor
	     
	     // Set createdByType to "admin" since an admin is creating this donor
	     user.setCreatedByType("Admin");
	     
	  // Set the createdAt field to the current time
	     user.setCreatedAt(LocalDateTime.now());  // Set the creation timestamp
	     
	     // Set isVerified to false
	     user.setIsVerified(false);

	     // Save donor information to the database
	     userRepository.save(user);

	     // Generate and send OTP to the user's mobile number
	     String otp = otpService.generateOtp(user.getMobile());
	     
	     
	  // Send OTP via email
	     String emailMessage = "Your OTP for Regristration  is : <b>" + otp + "</b>. For Mobile Number: <b>" + user.getMobile() + "</b>";
	     //emailService.sendOtp(user.getEmailid(),  "Your OTP Code", emailMessage);
	     emailService.sendHtmlEmail(user.getEmailid(), "Your OTP Code", emailMessage);
	     
	     // Send OTP via SMS using SMSCountry API
		    // String message = "User Admin login OTP is " + otp + " - SMSCNT";
		     String message = "Your Registration OTP is " + otp + " - EKTHA PVT LTD";
		     smsService.sendJsonSms(user.getMobile(), message);

	     // Set message for OTP verification
	     model.addAttribute("mobile", user.getMobile());
	     model.addAttribute("message", "OTP sent to your mobile number/ Email and valids for 2minutes");

	     // Proceed to the OTP verification page
	     return "donorresgisterotp"; // Redirect to OTP verification page
	 }
	 	 
	 @PostMapping("/validatedonorregisterOtp")
	 public String validatedonorregisterOtp(@RequestParam String mobile, @RequestParam String otp, HttpSession session, Model model) {
		 // Check if OTP is valid and not expired
		    if (otpService.isOtpExpired(mobile)) {
		        model.addAttribute("message", "OTP has expired. Please request a new one.");
		        model.addAttribute("mobile", mobile);
		        model.addAttribute("showResendButton", true); // Add flag to display resend button
		        return "otpr"; // Return to OTP page with the resend button
		    }
		 
	     if (otpService.validateOtp(mobile, otp)) {
	         // Skip the user lookup and directly set session attributes if OTP is valid
	         session.setAttribute("mobile", mobile);
	         System.out.println("Mobile set in session: " + mobile); // Debug statement
	         
	         // Redirect to admin home after successful OTP validation
	         return "redirect:/adminhome";
	     } else {
	         // OTP is invalid, return the error message and remain on the same page
	         model.addAttribute("message", "Invalid OTP");
	         model.addAttribute("mobile", mobile);
	         return "donorresgisterotp";
	     }
	 }
	 
	 @PostMapping("/resendOtpra")
	 public String resendOtpra(@RequestParam String mobile, Model model) {
	     String newOtp = otpService.generateOtp(mobile);

	     // Send new OTP via email
	     User user = userService.findByMobile(mobile);
	     // Send OTP via email
	     String emailMessage = "Your OTP for Regristration  is : <b>" + newOtp + "</b>. For Mobile Number: <b>" + mobile + "</b>";
	     //emailService.sendOtp(user.getEmailid(),  "Your OTP Code", emailMessage);
	     emailService.sendHtmlEmail(user.getEmailid(), "Your OTP Code", emailMessage);
	     
	     // Send OTP via SMS using SMSCountry API
		    // String message = "User Admin login OTP is " + otp + " - SMSCNT";
		  //   String message = "Your Registration OTP is " + newOtp + " - EKTHA PVT LTD";
		//     smsService.sendJsonSms(mobile, message);

	     

	     model.addAttribute("message", "A new OTP has been sent to your mobile.");
	     model.addAttribute("mobile", mobile);
	     return "otpr";
	 }
		
	 /* ============================Login process from donorside====================================== */
		 
	 @GetMapping("/donorlogin")
	    public String showDonorHomePage(Model model) {
	        // Add any necessary attributes to the model
	        return "donorlogin";
	    }  
	
	 @PostMapping("/login")
	 public String login(@RequestParam String mobile, Model model) {
	     // Find user by mobile number
	     User user = userRepository.findByMobile(mobile);
	     
	     // If user does not exist, return an error message
	     if (user == null) {
	         model.addAttribute("message", "User not found");
	         return "donorlogin";
	     }
	     
	     // Check if user is deleted
	     if (user.isDeleted()) {  // Assuming isDeleted is a boolean flag
	         model.addAttribute("message", "Your account has been deleted. Please contact support.");
	         return "donorlogin";
	     }

	     // Generate OTP
	     String otp = otpService.generateOtp(mobile);

	     // Send OTP via email
	     String emailMessage = "Your OTP for Donor Login is : <b>" + otp + "</b>. For Mobile Number: <b>" + mobile + "</b>";
	     //emailService.sendOtp(user.getEmailid(),  "Your OTP Code", emailMessage);
	     emailService.sendHtmlEmail(user.getEmailid(), "Your OTP Code", emailMessage);
	     
	     // Send OTP via SMS using SMSCountry API
	    // String message = "User Admin login OTP is " + otp + " - SMSCNT";
	   //  String message = "Your Donor Login OTP is " + otp + " - EKTHA PVT LTD";
	  //   smsService.sendJsonSms(mobile, message);

	     // Add mobile number to model and redirect to OTP input page
	     model.addAttribute("mobile", mobile);
	     model.addAttribute("message", "OTP sent to your mobile number/ Email and valids for 2minutes");
	     return "otp";
	 }

   	   
	 @PostMapping("/validateOtp")
	 public String validateOtp(@RequestParam String mobile, @RequestParam String otp, HttpSession session, Model model) {
	     // Check if OTP is valid and not expired
	     if (otpService.isOtpExpired(mobile)) {
	         model.addAttribute("message", "OTP has expired. Please request a new one.");
	         model.addAttribute("mobile", mobile);
	         model.addAttribute("showResendButton", true); // Add flag to display resend button
	         return "otp"; // Return to OTP page with the resend button
	     }

	     if (otpService.validateOtp(mobile, otp)) {
	         User user = userService.findByMobile(mobile);
	         if (user != null) {
	             // Update user details
	             user.setCreatedBy(user.getId());  // Set the createdBy field to the current user's ID
	             user.setCreatedByType("Self");   // Set createdByType to "Self"
	             user.setCreatedAt(LocalDateTime.now()); // Set the creation timestamp
	             user.setIsVerified(true);       // Set isVerified to true
	             userService.save(user);         // Save the updated user entity

	             // Set session attributes
	             session.setAttribute("userId", user.getId());
	             session.setAttribute("userName", user.getDonorname());
	             System.out.println("User ID set in session: " + user.getId());
	             System.out.println("userName set in session: " + user.getDonorname());

	             // Send a thank-you email with a link
	             try {
	                 String subject = "Welcome to Donor Portal!";
	                 String link = "http://localhost:8082/mydonations"; // Your link
	                 String message = "<p>Dear " + user.getDonorname() + ",</p>"
	                         + "<p>Thank you for registering and verifying your account. Please add your last donation details, if any :</p>"
	                         + "<p><a href='" + link + "'>View My Donations</a></p>"
	                         + "<p>Best regards,<br>Your Donor Portal Team</p>";
	                 emailService.sendHtmlEmail(user.getEmailid(), subject, message);
	                 System.out.println("Thank-you email with link sent to: " + user.getEmailid()); // Debug statement
	             } catch (Exception e) {
	                 System.err.println("Failed to send thank-you email: " + e.getMessage());
	             }

	             // Redirect to the donor home page after successful OTP validation and update
	             return "redirect:/donorhome";
	         } else {
	             model.addAttribute("message", "User not found");
	             return "otp"; // Return to OTP page if user is not found
	         }
	     } else {
	         model.addAttribute("message", "Invalid OTP");
	         model.addAttribute("mobile", mobile);
	         return "otp"; // Return to OTP page if OTP is invalid
	     }
	 }


	 
	 @PostMapping("/resendOtp")
	 public String resendOtp(@RequestParam String mobile, Model model) {
	     String newOtp = otpService.generateOtp(mobile);

	     // Send new OTP via email
	     User user = userService.findByMobile(mobile);
	     // Send OTP via email
	     String emailMessage = "Your OTP for Regristration  is : <b>" + newOtp + "</b>. For Mobile Number: <b>" + mobile + "</b>";
	     //emailService.sendOtp(user.getEmailid(),  "Your OTP Code", emailMessage);
	     emailService.sendHtmlEmail(user.getEmailid(), "Your OTP Code", emailMessage);
	     
	     // Send OTP via SMS using SMSCountry API
		    // String message = "User Admin login OTP is " + otp + " - SMSCNT";
		 //    String message = "Your Registration OTP is " + newOtp + " - EKTHA PVT LTD";
		  //   smsService.sendJsonSms(mobile, message);

	     

	     model.addAttribute("message", "A new OTP has been sent to your mobile.");
	     model.addAttribute("mobile", mobile);
	     return "otp";
	 }


	 /* ============================Donorhome====================================== */
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

	 /* ============================View Profile process ====================================== */   
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
	   
	    /* ============================Logout Profile process ====================================== */  
	    
	    @GetMapping("/logout")
	    public String logout(HttpSession session) {
	        session.invalidate(); // Invalidate the session to log out the user
	        
	        return "redirect:/home"; // Redirect to the login page or home page
	    }
	
	  
	    /* ============================All Donor data for Admin ====================================== */   
	    @GetMapping("/alldonorsdata")
	    public String getAllUsers(Model model, @RequestParam(defaultValue = "0") int page) {
	        int pageSize = 10; // Define the number of users per page

	        // Fetch the paginated list of users
			/* List<User> users = userService.getAllUsers(page, pageSize); */
	        Page<User> userPage = userService.getAllUsers(page, pageSize);

	        long totalUsers = userService.countAllRequests(); // Count the total number of users
	        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

	        // Add necessary attributes to the model
	        model.addAttribute("users", userPage.getContent());           // List of users on current page
	        model.addAttribute("currentPage", page);      // Current page number
	        model.addAttribute("totalPages", totalPages); // Total number of pages
	        model.addAttribute("pageSize", pageSize);     // Page size

	        return "alldonorsdata"; // The Thymeleaf template for displaying users
	    }
	    
	    
	    /* ============================Search for Blood process ====================================== */   	    
	    @GetMapping("/searchforblood")
	    public String searchForBlood(
	            @RequestParam(value = "bloodgroup", required = false) String bloodgroup,
	            @RequestParam(value = "city", required = false) String city,
	            @RequestParam(value = "state", required = false) String state,
	            @RequestParam(value = "hospital", required = false) String hospitalName,
	            Model model, HttpSession session) {

	        boolean searchPerformed = false;
	        List<User> results = new ArrayList<>();
	        String message = null;
	        String emailMessage = null;

	        // Retrieve userId from session
	        Long userId = (Long) session.getAttribute("userId");
	        if (userId != null) {
	            User loggedInUser = userService.findById(userId);
	            if (loggedInUser != null) {
	                model.addAttribute("user", loggedInUser);

	                // Perform search only if any of the parameters are provided
	                if (bloodgroup != null || city != null || state != null) {
	                    searchPerformed = true;

	                    // Perform the search based on provided criteria
	                    results = userService.findByBloodgroupAndCityAndState(bloodgroup, city, state);

	                    // Debugging: Print the results before filtering
	                    System.out.println("Results before filtering: ");
	                    results.forEach(user -> System.out.println(user.getBloodgroup() + " - " + user.getCity() + " - " + user.getState()));

	                    // Check if no results were found matching the criteria
	                    if (results.isEmpty() && message == null) {
	                        message = "No donors found matching the specified criteria. Please try again.";
	                    }

	                    // Filter out the logged-in user from the results list
	                    results = results.stream()
	                            .filter(user -> !user.getId().equals(loggedInUser.getId()))  // Exclude logged-in user
	                            .collect(Collectors.toList());

	                    // Save the search request to the repository
	                    searchRequestRepository.saveSearchRequest(userId, bloodgroup, city, state);

	                    // Send an email to each user in the filtered search results (excluding logged-in user)
	                    for (User user : results) {
	                        try {
	                            sendEmailToUser(user, loggedInUser, bloodgroup, hospitalName);
	                        } catch (Exception e) {
	                            System.out.println("Error sending email to user: " + user.getId());
	                            e.printStackTrace();
	                        }
	                    }

	                    // Set the email sent message after emails are sent
	                    emailMessage = "Emails have been successfully sent to the donors.";
	                }
	            } else {
	                model.addAttribute("error", "User not found.");
	                return "home";
	            }
	        } else {
	            model.addAttribute("error", "No user logged in.");
	            return "donorlogin";
	        }

	        // Add results, messages, and other necessary model attributes
	        model.addAttribute("results", results);
	        model.addAttribute("searchPerformed", searchPerformed);
	        model.addAttribute("message", message);  // Add the message to the model (this will show if no donors or self-donor case)
	        model.addAttribute("emailMessage", emailMessage); // Add email sent message to the model (only if emails were sent)
	        model.addAttribute("cities", userService.getAllCities());
	        model.addAttribute("states", userService.getAllStates());

	        return "searchforblood";
	    }


	    public DonorController(SentEmailRepository sentEmailRepository) {
	        this.sentEmailRepository = sentEmailRepository;
	    }
	  
	    private void sendEmailToUser(User user, User loggedInUser, String bloodgroup, String hospitalName) {
	        String subject = "Blood Search Alert";
	        String confirmationUrl = generateConfirmationUrl(user.getId(), loggedInUser.getId(), hospitalName);


	        String locationInfo = (hospitalName != null && !hospitalName.isEmpty()) ? 
	                "The blood is needed at " + hospitalName : 
	                "The blood is urgently needed.";

	        String message = String.format(
	            "Dear %s,\n\nUser %s is searching for blood group %s in %s, %s. %s\n\n" +
	            "Contact Information:\nName: %s\nMobile: %s\nEmail: %s\n\n" +
	            "If you are willing to donate, please confirm by clicking the following link:\n%s\n\nThank you.",
	            user.getDonorname(), loggedInUser.getDonorname(), bloodgroup, // Use the searched blood group
	            loggedInUser.getCity(), loggedInUser.getState(), locationInfo,
	            loggedInUser.getDonorname(), loggedInUser.getMobile(), loggedInUser.getEmailid(),
	            confirmationUrl
	        );

	        System.out.println("Generated Email Message:\n" + message);
	        emailService.sendEmail(user.getEmailid(), subject, message);

	        SentEmail sentEmail = new SentEmail(null, user.getEmailid(), LocalDateTime.now(), confirmationUrl, user.getId(), loggedInUser.getId());
	        sentEmailRepository.save(sentEmail);
	    }

	    
	   // private String generateConfirmationUrl(Long recipientId, Long loggedInUserId) {
	  //      return "http://localhost:8082/confirmRequest?recipientId=" + recipientId + "&loggedInUserId=" + loggedInUserId;
	  //  }  
	    
	    private String generateConfirmationUrl(Long recipientId, Long loggedInUserId, String hospitalName) {
	        String token = generateSecureToken(recipientId, loggedInUserId);
	        String encodedHospitalName = URLEncoder.encode(hospitalName, StandardCharsets.UTF_8);  // Encode hospital name for URL
	        //return "http://localhost:8082/confirmRequest?token=" + token + "&hospitalName=" + encodedHospitalName;
	        return "http://192.168.0.205:8082/confirmRequest?token=" + token + "&hospitalName=" + encodedHospitalName;
	    }

	    
	 // Method to generate a secure token (could use JWT or encryption)
	    private String generateSecureToken(Long recipientId, Long loggedInUserId) {
	        String data = recipientId + ":" + loggedInUserId;
	        return Base64.getUrlEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
	    }
	   


	    /* ============================Edit Profile process ====================================== */   
	    @PostMapping("/updateProfile")
	    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
	        // Save change logs here if necessary
	        userService.updateUser(user); // Your service method to update user data
	        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
	        return "redirect:/viewprofile"; // Redirect back to the profile page
	    }
	    
 /* ============================================Upload CSV/Excel  process ====================================== */   	 
	    @GetMapping("/upload")
	    public String showUploadPage(Model model) {
	        // Clear the model to ensure no previous data is displayed
	        model.addAttribute("message", model.containsAttribute("message") ? model.getAttribute("message") : null);
	        model.addAttribute("duplicateMessage", model.containsAttribute("duplicateMessage") ? model.getAttribute("duplicateMessage") : null);
	        model.addAttribute("users", model.containsAttribute("users") ? model.getAttribute("users") : new ArrayList<>()); // Clear user list
	        return "upload"; // Return the upload page
	    }

	    // Handle file upload for both Excel and CSV
	    @PostMapping("/upload")
	    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType, RedirectAttributes redirectAttributes) {
	        try {
	            UserUploadResult result = excelService.parseFile(file, fileType);
	            excelService.saveUsersFromFile(file, fileType); // Save valid users

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

/* ============================================Download Excel &PDF process ====================================== */   	 
	    	    //Download excel
	    @GetMapping("/donorusers/exceldownload")
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
	    
	    //Download PDF
	    @GetMapping("/donorusers/pdfdownload")
	    public ResponseEntity<byte[]> downloadUserDataAsPdf() throws IOException {
	        List<User> users = userService.getAllUsers(); // Fetch all users from database

	        // Create a byte array output stream to hold PDF data
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	        // Initialize PDF document with A4 page size
	        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf, PageSize.A4.rotate()); // Use A4 landscape page size

	        // Add a title
	       Paragraph title = new Paragraph("Donor Users Data")
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(18)
	            .setBold();
	        document.add(title);

	        // Create a table with appropriate column widths
	       Table table = new Table(new float[]{1, 2, 2, 3, 2, 2, 1, 1, 3, 2, 2});
	        table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

	        // Add table headers
	        String[] headers = {"ID", "Name", "Mobile", "Email", "Date of Birth", "Blood Group", "Age", "Gender", "Address", "City", "State"};
	        for (String header : headers) {
	            // Correctly instantiate Cell using the constructor
	            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(header).setBold().setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)));
	        }

	        // Add user data rows
	        for (User user : users) {
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(user.getId()))));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getDonorname())));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getMobile())));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getEmailid())));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getDateofbirth().toString())));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getBloodgroup())));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(user.getAge()))));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getGender())));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getAddress())));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getCity())));
	            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(user.getState())));
	        }

	        document.add(table);
	        document.close();

	        // Prepare the PDF for download
	        byte[] pdfData = byteArrayOutputStream.toByteArray();
	        HttpHeaders headers1 = new HttpHeaders();
	        headers1.add("Content-Disposition", "attachment; filename=donorusers.pdf");
	        headers1.add("Content-Type", "application/pdf");

	        return ResponseEntity.ok()
	                .headers(headers1)
	                .body(pdfData);
	    }


	  	

	    
	  }

	   	

