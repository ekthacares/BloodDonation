package com.example.ekthacare.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ekthacare.entity.BloodDonation;
import com.example.ekthacare.entity.BloodRequest;
import com.example.ekthacare.entity.Campaigns;
import com.example.ekthacare.entity.Confirmation;
import com.example.ekthacare.entity.NotificationRequest;
import com.example.ekthacare.entity.SentEmail;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.firebase.FirebaseInit;
import com.example.ekthacare.repo.CampaignsRepository;
import com.example.ekthacare.repo.NotificationRequestRepository;
import com.example.ekthacare.repo.SentEmailRepository;
import com.example.ekthacare.repo.UserRepository;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.BloodRequestService;
import com.example.ekthacare.services.CampaignsService;
import com.example.ekthacare.services.ConfirmationService;
import com.example.ekthacare.services.EmailService;
import com.example.ekthacare.services.OtpService;
import com.example.ekthacare.services.SearchRequestService;
import com.example.ekthacare.services.SentEmailService;
import com.example.ekthacare.services.UserService;
import com.example.ekthacare.util.JwtUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;


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
	    
	    @Autowired
	    private BloodDonationService bloodDonationService;
	    
	    @Autowired
	    private final SentEmailService sentEmailService;
	    
	    @Autowired
	    private BloodRequestService bloodRequestService;
	    
	    
	    
	    public APIController(SentEmailService sentEmailService) {
	        this.sentEmailService = sentEmailService;
	    }
	    
	    @Autowired
	    private SearchRequestService searchRequestRepository;
	    
	    
	    @Autowired
	    private  SentEmailRepository sentEmailRepository;
	    
	    @Autowired
	    private ConfirmationService confirmationService;
	    
	    
	    @Autowired
	    private NotificationRequestRepository notificationRequestRepository;
	    
	    @Autowired
	    private CampaignsRepository campaignsRepository;
	    
	    @Autowired
	    private CampaignsService campaignsService;

	
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

	    
	    @PostMapping("/app/register")
	    public ResponseEntity<Map<String, Object>> appregisterDonor(@RequestBody User user) {
	        Map<String, Object> response = new HashMap<>();	       

	        // Step 2: If the mobile number doesn't exist, proceed with saving the user
	        user.setIsVerified(false);  // Assuming user should be verified later

	        // Step 3: Save donor information to the database
	 
	            userRepository.save(user); // Save the user only if mobile is unique
	        

	        // Step 4: Generate and send OTP to the user's mobile number
	        String otp = otpService.generateOtp(user.getMobile());

	        // Step 5: Send OTP via email
	        try {
	            String emailMessage = "Your OTP for Registration is: <b>" + otp + "</b>. For Mobile Number: <b>" + user.getMobile() + "</b>";
	            emailService.sendHtmlEmail(user.getEmailid(), "Your OTP Code", emailMessage);
	        } catch (Exception e) {
	            response.put("status", "error");
	            response.put("message", "Failed to send email. Please try again.");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }

	        // Step 6: (Optional) Send OTP via SMS if applicable
	        try {
	            // Uncomment and use if you're sending SMS as well
	            // String message = "Your Registration OTP is " + otp + " - EKTHA PVT LTD";
	            // smsService.sendJsonSms(user.getMobile(), message);
	        } catch (Exception e) {
	            // Handle SMS sending failure (optional)
	        }

	        // Step 7: Prepare success response
	        response.put("status", "success");
	        response.put("message", "OTP sent to your mobile number/email and valid for 2 minutes.");
	        response.put("mobile", user.getMobile());

	        return ResponseEntity.ok(response); // Return success response
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
	    
	    @GetMapping("/appuser")
		  @ResponseBody
		  public User getUser(@RequestParam Long id) {
		      return userService.findById(id);
		  }
	    
	  
	 // In your Spring Boot Controller
	    @PostMapping("/app/updateProfile")
	    public ResponseEntity<Map<String, String>> appupdateProfile(@RequestBody User user) {
	        try {
	            // Log incoming data for debugging
	            System.out.println("Incoming User Data: " + user);
	            userService.updateUser(user);

	            // Create a JSON response
	            Map<String, String> response = new HashMap<>();
	            response.put("message", "Profile updated successfully!");

	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            e.printStackTrace();

	            // Return an error message in JSON format
	            Map<String, String> errorResponse = new HashMap<>();
	            errorResponse.put("message", "Error updating profile.");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	        }
	    }
	    
	
	    
	    @GetMapping("/app/mydonations")
	    public ResponseEntity<Map<String, Object>> getDonations(	           
	            @RequestParam("userId") Long userId) {

	        Map<String, Object> response = new HashMap<>();

	        // Validate token, fetch user from database, and check userId etc. Here we just proceed with userId validation
	        if (userId == null) {
	            response.put("message", "User ID is missing");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        // Fetch donations using the service
	        java.util.List<BloodDonation> donations = bloodDonationService.getAllDonations(userId);

	        if (donations.isEmpty()) {
	            response.put("message", "No donations found");
	        } else {
	            response.put("donations", donations);
	        }

	        return ResponseEntity.ok(response);
	    }
	
	    @PostMapping("/app/addDonation")
	    public ResponseEntity<Map<String, Object>> addDonation(
	            @RequestParam("userId") Long userId,
	            @RequestParam(value = "lastDonationDate", required = false)
	            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDonationDate,
	            @RequestParam("hospitalName") String hospitalName) {

	        Map<String, Object> response = new HashMap<>();

	        if (userId == null) {
	            response.put("message", "User ID is missing");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        if (hospitalName == null || hospitalName.trim().isEmpty()) {
	            response.put("message", "Hospital name is missing");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        if (lastDonationDate == null) {
	            response.put("message", "Last donation date is missing");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        // Find the user by ID (make sure user exists in the database)
	        User user = userService.findById(userId);
	        if (user == null) {
	            response.put("message", "User not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }

	        // Create a new donation record
	        bloodDonationService.createNewDonationRecord(userId, null, lastDonationDate, hospitalName);

	        response.put("message", "Donation added successfully");
	        return ResponseEntity.ok(response);
	    }
	   
	    
	    @GetMapping("/app/sentEmails")
	    public ResponseEntity<?> getSentEmails(
	        @RequestHeader("Authorization") String authorizationHeader, 
	        @RequestHeader("userId") Long userId) {

	        try {
	            // Check if the Authorization header is present and starts with "Bearer "
	            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing or invalid.");
	            }

	            // Extract the JWT token from the Authorization header
	            String jwtToken = authorizationHeader.substring(7); // "Bearer " is 7 characters

	            // Ensure userId is valid
	            if (userId == null || userId <= 0) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid userId.");
	            }

	            // Find the logged-in user using the userId
	            User loggedInUser = userService.findById(userId);
	            System.out.println("loggedInUser userId from header: " + userId);

	            if (loggedInUser == null) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
	            }

	            // Retrieve all sent emails where the logged-in user is the recipient
	            java.util.List<SentEmail> sentEmails = sentEmailService.getSentEmailsByRecipientId(userId);
	            System.out.println("Number of sent emails found for recipient: " + sentEmails.size());

	            if (sentEmails.isEmpty()) {
	                return ResponseEntity.ok(new ArrayList<SentEmail>()); // Return an empty list
	            }

	            // Return sent emails as JSON
	            return ResponseEntity.ok(sentEmails);

	        } catch (Exception e) {
	            // Handle unexpected errors
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
	        }
	    }

	    	
	    @GetMapping("/app/searchforblood")
	    public ResponseEntity<Map<String, Object>> searchForBlood(
	            @RequestParam(value = "bloodgroup", required = false) String bloodgroup,
	            @RequestParam(value = "city", required = false) String city,
	            @RequestParam(value = "state", required = false) String state,
	            @RequestParam(value = "hospital", required = false) String hospitalName,
	            @RequestHeader("Authorization") String authorizationHeader, 
	            @RequestHeader("userId") Long userId) { 

	        Map<String, Object> response = new HashMap<>();
	        boolean searchPerformed = false;
	        java.util.List<User> results = new ArrayList<>();
	        String message = null;
	        String emailMessage = null;

	        try {
	            // Check if the Authorization header is present and starts with "Bearer "
	            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	                response.put("error", "Authorization token is missing or invalid.");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	            }

	            // Extract the JWT token from the Authorization header
	            String jwtToken = authorizationHeader.substring(7); // "Bearer " is 7 characters
	            
	            // You can add JWT token validation logic here (e.g., verify the token using JWT library)
	            if (userId != null) {
	                User loggedInUser = userService.findById(userId);
	                if (loggedInUser != null) {
	                    response.put("user", loggedInUser);

	                    // Simulate some logic to perform the search
	                    if (bloodgroup != null || city != null || state != null) {
	                        searchPerformed = true;

	                        // Perform the search based on provided parameters
	                        results = userService.findByBloodgroupAndCityAndState(bloodgroup, city, state);

	                     // Debugging: Print the results before filtering
		                    System.out.println("Results before filtering: ");
		                    results.forEach(user -> System.out.println(user.getBloodgroup() + " - " + user.getCity() + " - " + user.getState()));

		                    

		                    // Filter out the logged-in user from the results list
		                    results = results.stream()
		                            .filter(user -> !user.getId().equals(loggedInUser.getId()))  // Exclude logged-in user
		                            .collect(Collectors.toList());
		                 // Check if no results were found matching the criteria
		                    if (results.isEmpty()) {
		                    	message = "No donors found matching the specified criteria. Please try again.";
		                    } else {
		                    // Save the search request to the repository
		                    searchRequestRepository.saveSearchRequest(userId, bloodgroup, city, state);

	                        // Send emails logic (optional)
		                    for (User user : results) {
		                        try {
		                            sendEmailToUser(user, loggedInUser, bloodgroup, hospitalName);
		                        } catch (Exception e) {
		                            System.out.println("Error sending email to user: " + user.getId());
		                            e.printStackTrace();
		                        }
		                    }
	                        emailMessage = "Emails have been successfully sent to the donors.";
	                    }
	                } else {
	                    // Handle case when logged-in user is not found
	                    response.put("error", "User not found.");
	                    return ResponseEntity.status(404).body(response);
	                }
	            } else {
	                // Handle case when no user is logged in
	                response.put("error", "No user logged in.");
	                return ResponseEntity.status(401).body(response);  // Unauthorized
	            }

	        } 
	        }catch (Exception e) {
	            // Handle general errors
	            response.put("error", "An unexpected error occurred: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }

	        // Populate response with the results
	        response.put("searchPerformed", searchPerformed);
	        response.put("results", results);
	        response.put("message", message);
	        response.put("emailMessage", emailMessage);

	        return ResponseEntity.ok(response);
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
	            user.getDonorname(), loggedInUser.getDonorname(), bloodgroup, 
	            loggedInUser.getCity(), loggedInUser.getState(), locationInfo,
	            loggedInUser.getDonorname(), loggedInUser.getMobile(), loggedInUser.getEmailid(),
	            confirmationUrl
	        );

	        System.out.println("Generated Email Message:\n" + message);
	        emailService.sendEmail(user.getEmailid(), subject, message);

	        SentEmail sentEmail = new SentEmail(null, user.getEmailid(), LocalDateTime.now(), confirmationUrl, user.getId(), loggedInUser.getId());
	        sentEmailRepository.save(sentEmail);
	        
	        // Now send the push notification
	        sendPushNotification(user.getId(), user.getFcmToken(), "Blood Search Alert", "You have request to donate blood", "Notification");
	        
	    }

	    private String generateConfirmationUrl(Long recipientId, Long loggedInUserId, String hospitalName) {
	        String token = generateSecureToken(recipientId, loggedInUserId);
	        String encodedHospitalName = URLEncoder.encode(hospitalName, StandardCharsets.UTF_8);  // Encode hospital name for URL
	        return "http://192.168.29.205:8082/confirmRequest?token=" + token + "&hospitalName=" + encodedHospitalName;
	    }

	    private String generateSecureToken(Long recipientId, Long loggedInUserId) {
	        String data = recipientId + ":" + loggedInUserId;
	        return Base64.getUrlEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
	    }
	    
	    private void sendPushNotification(Long userId,String fcmToken, String title, String message, String type) {
	    	System.out.println("User FCM Token: " + fcmToken);
	    	// Check if the FCM token is missing or invalid
	        	        try {
	            // Initialize Firebase (Make sure it's done before sending push notifications)
	            FirebaseInit.initialize();
	            
	            // Retrieve the recipient's FCM token using the userId
	            if (fcmToken == null || fcmToken.isEmpty()) {
	                // Get the FCM token for the recipient user from the database
	                fcmToken = userRepository.findFcmTokenById(userId);
	                if (fcmToken == null || fcmToken.isEmpty()) {
	                    System.err.println("FCM token not found for User ID: " + userId);
	                    return;  // Exit if the token is not found
	                }
	            }

	            // Build the notification using Notification.Builder
	            Notification notification = Notification.builder()
	                    .setTitle(title)
	                    .setBody(message)
	                    .build();

	            // Build the message to send using FCM
	            Message pushMessage = Message.builder()
	                    .setToken(fcmToken)  // FCM token of the user
	                    .putData("userId", String.valueOf(userId)) // ✅ Include userId in FCM payload
	                    .putData("title", title)
	                    .putData("body", message)
	                    .putData("type", type) // 🔹 Important: Differentiates between "notification" & "campaign"
	                    .build();

	            // Send the notification
	            String response = FirebaseMessaging.getInstance().send(pushMessage);

	            // Log the response from FCM (you can store it in the database if needed)
	            System.out.println("Successfully sent push notification: " + response);
	            
	            // Save the notification in the database
	            NotificationRequest dbNotification = new NotificationRequest();
	            dbNotification.setUserId(userId); // ✅ Save the user ID
	            dbNotification.setTitle(title);
	            dbNotification.setMessage(message);
	            dbNotification.setFcmToken(fcmToken);
	            dbNotification.setSentTime(LocalDateTime.now());  // Set the timestamp when the notification was sent

	            // Save the notification in the repository (database)
	            notificationRequestRepository.save(dbNotification);

	            
	        } catch (Exception e) {
	            // Handle errors
	            System.err.println("Error sending push notification: " + e.getMessage());
	        }
	    }

	    
	    @GetMapping("/app/donortracking")
	    public ResponseEntity<?> getDonationTrackingForLoggedInUser(
	            @RequestHeader("Authorization") String authorizationHeader,
	            @RequestHeader("userId") Long userId) {
	        
	        Map<String, Object> response = new HashMap<>();

	        // Log request details for debugging
	        System.out.println("Authorization Header: " + authorizationHeader);
	        System.out.println("userId Header: " + userId);

	        try {
	            // Validate Authorization header
	            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	                response.put("error", "Authorization token is missing or invalid.");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	            }

	            // Extract the JWT token from the Authorization header
	            String jwtToken = authorizationHeader.substring(7); // Remove "Bearer " prefix
	            // Optional: You can validate the JWT token here if needed.

	            // Validate userId
	            if (userId == null) {
	                response.put("message", "User not logged in.");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	            }

	            // Find the user
	            User loggedInUser = userService.findById(userId);
	            if (loggedInUser == null) {
	                response.put("message", "User not found.");
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	            }

	            // Retrieve confirmations for the logged-in user
	            java.util.List<Confirmation> confirmations = confirmationService.getConfirmationsByLoggedInUserId(userId);
	            if (confirmations == null || confirmations.isEmpty()) {
	                response.put("message", "No donation tracking information available.");
	                response.put("user", loggedInUser);
	                return ResponseEntity.ok(response);
	            }

	            // Construct the successful response
	            response.put("user", loggedInUser);
	            response.put("confirmations", confirmations);
	            return ResponseEntity.ok(response);

	        } catch (Exception ex) {
	            // Handle any unexpected errors
	            response.put("error", "An unexpected error occurred: " + ex.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }
	    }
	    	
	    
	    @PostMapping("/users/{id}/fcm-token")
	    public ResponseEntity<?> updateFcmToken(@PathVariable("id") Long userId, @RequestBody Map<String, String> requestBody) {
	        String fcmToken = requestBody.get("fcmToken");  // Ensure key matches the one sent from Android

	        if (fcmToken == null || fcmToken.isEmpty()) {
	            return ResponseEntity.badRequest().body("FCM token is missing.");
	        }

	        userService.updateFcmToken(userId, fcmToken);  
	        return ResponseEntity.ok().build();
	    }
	    
	    @GetMapping("/users/{id}/fcm-token")
	    public ResponseEntity<?> getFcmToken(@PathVariable("id") Long userId) {
	        System.out.println("Fetching FCM token for user ID: " + userId);

	        String fcmToken = userService.getFcmTokenByUserId(userId);

	        if (fcmToken == null || fcmToken.isEmpty()) {
	            System.out.println("FCM token not found for user ID: " + userId);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FCM token not found for user ID: " + userId);
	        }

	        System.out.println("FCM Token for user " + userId + ": " + fcmToken);
	        return ResponseEntity.ok(Collections.singletonMap("fcmToken", fcmToken));
	    }


	    
	    
	    @PostMapping("/app/request")    
	    
	    public ResponseEntity<Map<String, String>> submitRequest(@RequestBody BloodRequest bloodRequest) {
	        bloodRequestService.saveBloodRequest(bloodRequest);

	        Map<String, String> response = new HashMap<>();
	        response.put("message", "Blood request submitted successfully!");

	        return ResponseEntity.ok(response);
	    }
	    
	    
	    @GetMapping("/campaigns")
	    public ResponseEntity<List<Campaigns>> getAllCampaigns() {
	        List<Campaigns> campaigns = campaignsRepository.findAll();
	        return ResponseEntity.ok(campaigns);
	    }
	    
	    @GetMapping("/campaigns/latest")
	    public ResponseEntity<List<Campaigns>> getLatestCampaigns() {
	        return ResponseEntity.ok(campaignsService.getLastTwoCampaigns());
	    }
	    
	   
	}



	
	    
	   
