package com.example.ekthacare.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.repo.UserRepository;
import com.example.ekthacare.services.EmailService;
import com.example.ekthacare.services.OtpService;
import com.example.ekthacare.services.UserService;
import com.example.ekthacare.util.JwtUtil;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/api")
public class APIController {
	
	 @Autowired
	    private UserRepository userRepository;
	 
	 @Autowired
	    private OtpService otpService;

	    @Autowired
	    private EmailService emailService;
	    
	    @Autowired
	    private UserService userService;
	    
	    @Autowired
	    private JwtUtil jwtUtil;
	    

	
	    @PostMapping("/app login")
	    public ResponseEntity<?> applogin(@RequestParam String mobile) {
	        Map<String, Object> response = new HashMap<>();

	        // Find user by mobile number
	        User user = userRepository.findByMobile(mobile);

	        // If user does not exist, return an error response
	        if (user == null) {
	            response.put("status", "error");
	            response.put("message", "User not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }

	        // Check if user is deleted
	        if (user.isDeleted()) {  // Assuming isDeleted is a boolean flag
	            response.put("status", "error");
	            response.put("message", "Your account has been deleted. Please contact support.");
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	        }

	        // Generate OTP
	        String otp = otpService.generateOtp(mobile);

	        // Send OTP via email
	        try {
	            String emailMessage = "Your OTP for Donor Login is: <b>" + otp + "</b>. For Mobile Number: <b>" + mobile + "</b>";
	            emailService.sendHtmlEmail(user.getEmailid(), "Your OTP Code", emailMessage);
	        } catch (Exception e) {
	            response.put("status", "error");
	            response.put("message", "Failed to send email. Please try again.");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }

	        // Uncomment if SMS service is implemented
	        /*
	        try {
	            String smsMessage = "Your Donor Login OTP is " + otp + " - EKTHA PVT LTD";
	            smsService.sendJsonSms(mobile, smsMessage);
	        } catch (Exception e) {
	            response.put("status", "error");
	            response.put("message", "Failed to send SMS. Please try again.");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }
	        */

	        // Return success response
	        response.put("status", "success");
	        response.put("message", "OTP sent to your registered email and is valid for 2 minutes.");
	        response.put("mobile", mobile); // Optional: Include the mobile number for client reference
	        return ResponseEntity.ok(response);
	    }


   	   
	    @PostMapping("/app/validateOtp")
	    public ResponseEntity<?> appValidateOtp(@RequestParam String mobile, @RequestParam String otp, HttpSession session) {
	        Map<String, Object> response = new HashMap<>();

	        // Check if OTP is expired
	        if (otpService.isOtpExpired(mobile)) {
	            response.put("status", "error");
	            response.put("message", "OTP has expired. Please request a new one.");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        // Validate OTP
	        if (otpService.validateOtp(mobile, otp)) {
	            User user = userService.findByMobile(mobile);
	            if (user != null) {
	                // Update user details
	                user.setCreatedBy(user.getId());
	                user.setCreatedByType("Self");
	                user.setCreatedAt(LocalDateTime.now());
	                user.setIsVerified(true);
	                
	                // Generate JWT token using mobile
	                String jwtToken = jwtUtil.generateToken(mobile);  // Generate the JWT token

	                // Print the JWT token to console for debugging
	                System.out.println("Generated JWT Token: " + jwtToken);  // This will print the token

	                // Save the JWT token in the user entity
	                user.setJwtToken(jwtToken);  // Save the token in the user object

	                // Save the updated user with the JWT token to the database
	                userService.save(user);  // Assuming userService handles the save logic

	                // Send confirmation email (optional)
	                try {
	                    String subject = "OTP Validation Successful";
	                    String message = "<p>Dear " + user.getDonorname() + ",</p>"
	                            + "<p>Your OTP validation was successful. Welcome to the platform!</p>"
	                            + "<p>Best regards,<br>Your Team</p>";
	                    emailService.sendHtmlEmail(user.getEmailid(), subject, message);
	                } catch (Exception e) {
	                    System.err.println("Failed to send email: " + e.getMessage());
	                }

	                // Add user details and JWT token to response
	                response.put("status", "success");
	                response.put("message", "OTP validated successfully.");
	                response.put("userId", user.getId());
	                response.put("donorname", user.getDonorname());
	                response.put("email", user.getEmailid());
	                response.put("sessionToken", session.getId());  // Optional for session handling
	                response.put("jwtToken", jwtToken);  // Include JWT token in response

	                return ResponseEntity.ok(response);
	            } else {
	                response.put("status", "error");
	                response.put("message", "User not found.");
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	            }
	        } else {
	            response.put("status", "error");
	            response.put("message", "Invalid OTP.");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }
	    }


	    @GetMapping("/app/viewProfile")
	    public ResponseEntity<?> viewProfile(HttpSession session, @RequestParam("userId") Long userId,
                @RequestParam("sessionToken") String sessionToken) {
	      
	        System.out.println("Retrieved userId from session: " + userId);

	        if (userId != null) {
	            User user = userService.findById(userId);
	            if (user != null) {
	                return ResponseEntity.ok(user); // Return user as JSON
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body("User not found.");
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("No user logged in.");
	        }
	    }
	    
	    
	    @GetMapping("/app/donorhome")
	    public ResponseEntity<User> getDonorHome(@RequestHeader(value = "Authorization", required = false) String authHeader) {
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            // Extract the JWT token from the Authorization header
	            String jwtToken = authHeader.substring(7);  // Remove "Bearer " prefix

	            try {
	                // Extract the mobile from the JWT token
	                String mobile = jwtUtil.extractMobile(jwtToken);

	                // Find the user based on the extracted mobile
	                User user = userRepository.findByMobile(mobile);
	                if (user != null) {
	                    return ResponseEntity.ok(user);  // Send JSON response with donor details
	                }
	            } catch (Exception e) {
	                // Handle any exceptions, like token expiration or invalid format
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	        }
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // Unauthorized if JWT token is missing/invalid
	    }


	   }
