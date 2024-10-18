package com.example.ekthacare.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ekthacare.entity.BloodRequest;
import com.example.ekthacare.entity.SearchRequest;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.Otp1Repository;
import com.example.ekthacare.repo.User1Repository;
import com.example.ekthacare.repo.UserRepository;
import com.example.ekthacare.services.BloodRequestService;
import com.example.ekthacare.services.EmailService;
import com.example.ekthacare.services.Otp1Service;
import com.example.ekthacare.services.SearchRequestService;
import com.example.ekthacare.services.SmsService;
import com.example.ekthacare.services.User1Service;
import com.example.ekthacare.services.UserService;

import jakarta.servlet.http.HttpSession;



@Controller
public class AdminController {
	
	 @Autowired
	    private User1Repository user1Repository;
	 
	 @Autowired
	    private UserRepository userRepository;
	 
	 @Autowired
	    private Otp1Service Otp1Service;
	 
	  @Autowired
	    private User1Service user1Service;
	 
	  @Autowired
	    private UserService userService;
	  
	  @Autowired
	    private EmailService emailService;
	  
	  @Autowired
	    private BloodRequestService bloodRequestService;
	  
	  @Autowired
	    private SearchRequestService searchRequestService;
	  
 
	  
	  @Autowired
	    private SmsService smsService;
	  
	 @GetMapping("/adminlogin")
	    public String showAdminHomePage(Model model) {
	        // Add any necessary attributes to the model
	        return "adminlogin";
	    }
	 
	


	
	 @GetMapping("/adminregister")
	    public String showRegistrationForm(Model model) {
	        model.addAttribute("user", new User1());
	        return "adminregister"; // This will render register.html
	    }
	 
	 @PostMapping("/adminregister")
	    public String registerAdmin(@ModelAttribute User1 user, Model model) {
		 // Check if the mobile number already exists
		    if (user1Repository.existsByMobile(user.getMobile())) {
		        model.addAttribute("message", "Mobile number already exists. Please use a different number.");
		        return "adminregister"; // Return to registration page with an error message
		    }
		 
		 // Set isVerified to false
	        user.setVerified(false);
	        // Save admin information to the database
	        user1Repository.save(user);

	        // Generate and send OTP to the user's mobile number
	        String otp = Otp1Service.generateOtp(user.getMobile());
	        sendOtpToMobile(user.getMobile(), otp);
	        emailService.sendOtp(user.getEmailid(), otp);
	        
	        // Store user in session for later use
	        model.addAttribute("mobile", user.getMobile());
	        model.addAttribute("message", "OTP sent to your mobile number. Please verify.");

	        return "adminotp"; // Redirect to OTP verification page
	    }

	    private void sendOtpToMobile(String mobile, String otp) {
	        // Implement SMS sending logic here
	        System.out.println("Sending OTP " + otp + " to mobile " + mobile);
	    }
	    

	    
	    @PostMapping("/adminlogin")
	    public String login(@RequestParam String mobile, Model model) {
	        User1 user = user1Repository.findByMobile(mobile);
	        if (user == null) {
	            model.addAttribute("message", "User not found");
	            return "adminloginerror";
	        }

	        String otp = Otp1Service.generateOtp(mobile);
	        emailService.sendOtp(user.getEmailid(), otp);
	     // Send OTP via SMS using SMSCountry API
	         String message = "User Admin login OTP is " + otp + " - SMSCNT";
	         smsService.sendJsonSms(mobile, message);
	        model.addAttribute("mobile", mobile);
	        return "adminloginotp";
	    }
		           
	 @PostMapping("/adminvalidateOtp")
	 public String validateOtp(@RequestParam String mobile, @RequestParam String otp, HttpSession session, Model model) {
	     if (Otp1Service.validateOtp(mobile, otp)) {
	         User1 user = user1Service.findByMobile(mobile);
	         if (user != null) {
	             session.setAttribute("userId", user.getId());
	             System.out.println("User ID set in session: " + user.getId()); // Debug statement
	             session.setAttribute("userName", user.getName());
	             return "redirect:/adminhome";
	         } else {
	             model.addAttribute("message", "User not found");
	             return "adminlogin";
	         }
	     } else {
	         model.addAttribute("message", "Invalid OTP");
	         model.addAttribute("mobile", mobile);
	         return "adminloginotp";
	     }
	 }
	 
	 @GetMapping("/adminhome")
	 public String showDonorHome(HttpSession session, Model model) {
	     Long userId = (Long) session.getAttribute("userId");
	     System.out.println("Retrieved userId from session: " + userId); // Debug statement
	     if (userId != null) {
	         User1 user = user1Service.findById(userId);
	         System.out.println("Retrieved user from database: " + user);
	         if (user != null) {
	             model.addAttribute("user", user);
	             model.addAttribute("userId", userId);
	             return "adminhome";
	         } else {
	             return "redirect:/auth/login";
	         }
	     }
	     return "redirect:/auth/login";
	 }
	 
	   @GetMapping("/adminviewprofile")
	    public String viewProfile(Model model, HttpSession session) {
	        Long userId = (Long) session.getAttribute("userId");
	        System.out.println("Retrieved userId from session1: " + userId);
	        if (userId != null) {
	            User1 user = user1Service.findById(userId);
	            if (userId != null) {
	                model.addAttribute("user", user);
	                System.out.println("Retrieved user from database1: " + user);
	                return "adminviewprofile";
	            } else {
	                model.addAttribute("error", "User not found.");
	                return "home";
	            }
	        } else {
	            model.addAttribute("error", "No user logged in.");
	            return "adminlogin";
	        }
	    }
	    
	    @GetMapping("/logout1")
	    public String logout(HttpSession session) {
	        session.invalidate(); // Invalidate the session to log out the user
	        return "redirect:/home"; // Redirect to the login page or home page
	    }
	    
	    @GetMapping("/alladmindata")
	    public String getAllUsers(Model model) {
	        List<User1> users = user1Service.getAllUsers();
	        model.addAttribute("users", users);
	        System.out.println("users data is  "+users);
	        return "alladmindata";
	    }
	    
	    @GetMapping("/viewbloodrequestdata")
	    public String viewRequests(Model model) {
	    	List<BloodRequest> bloodRequests = bloodRequestService.getAllBloodRequests();
		        model.addAttribute("bloodRequests", bloodRequests);
		        System.out.println("bloodRequests data is  "+bloodRequests);
	        return "viewbloodrequestdata";
	    }
	    
	    @GetMapping("/allsearchrequestdata")
	    public String viewAllSearchRequests(Model model) {
	    	
	        List<SearchRequest> searchRequests = searchRequestService.getAllSearchRequests();	       
	        model.addAttribute("searchRequests", searchRequests);
	        return "allsearchrequestdata";  // Name of the template
	    }
	    
	    
	  @GetMapping("/user/{userId}")
	   public String viewUserProfile(@PathVariable("userId") Long userId, Model model) {
	      User user = userService.findById(userId);
	       model.addAttribute("user", user);
	     return "userDetails"; // Name of the Thymeleaf template to display user profile
	       // return "fragments/userprofile :: userProfileFragment";
	   }

	  
	   
	  @GetMapping("/api/user")
	  @ResponseBody
	  public User getUser(@RequestParam Long id) {
	      return userService.findById(id);
	  }
	  }


