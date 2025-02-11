package com.example.ekthacare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ekthacare.entity.Superadmin;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.SuperAdminRepository;
import com.example.ekthacare.repo.User1Repository;
import com.example.ekthacare.services.EmailService;
import com.example.ekthacare.services.SmsService;
import com.example.ekthacare.services.SuperAdminService;
import com.example.ekthacare.services.SuperadminOtpService;
import com.example.ekthacare.services.User1Service;
import com.example.ekthacare.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SuperAdminController {
	
	@Autowired
    private SuperAdminRepository superadminRepository;
	
	@Autowired
    private SuperAdminService superadminService;
	
	@Autowired
    private SuperadminOtpService superadminotpService;
	
	 @Autowired
	    private EmailService emailService;
	
	    @Autowired
	    private SmsService smsService;
	    
	    @Autowired
	    private User1Service user1Service;
	    
	    @Autowired
	    private User1Repository user1Repository;
	    
	    @Autowired
	    private UserService userService;
	
	@GetMapping("/superadminlogin")
    public String showDonorHomePage(Model model) {
        // Add any necessary attributes to the model
        return "superadminlogin";
    }
	
	@PostMapping("/superadminlogin")
    public String login(@RequestParam String mobile, Model model) {
        Superadmin user = superadminRepository.findByMobile(mobile);
        if (user == null) {
            model.addAttribute("message", "User not found");
            return "loginerror";
        }

        // Generate OTP
        String otp = superadminotpService.generateOtp(mobile);

        // Send OTP via email
        emailService.sendOtp(user.getEmailid(), otp);
        
        // Send OTP via SMS using SMSCountry API
         String message = "User SuperAdmin login OTP is " + otp + " - SMSCNT";
         smsService.sendJsonSms(mobile, message);
        model.addAttribute("mobile", mobile);
        return "superadminloginotp";
    } 
	
	 @PostMapping("/superadminvalidateOtp")
	 public String validateOtp(@RequestParam String mobile, @RequestParam String otp, HttpSession session, Model model) {
	     if (superadminotpService.validateOtp(mobile, otp)) {
	    	 Superadmin user = superadminService.findByMobile(mobile);
	         if (user != null) {
	             session.setAttribute("userId", user.getId());
	             System.out.println("User ID set in session: " + user.getId()); // Debug statement
	             session.setAttribute("userName", user.getName());
	             System.out.println("userName set in session: " + user.getName());
	             return "redirect:/superadminhome";
	         } else {
	             model.addAttribute("message", "User not found");
	             return "superadminotp";
	         }
	     } else {
	         model.addAttribute("message", "Invalid OTP");
	         model.addAttribute("mobile", mobile);
	         return "superadminotp";
	     }
	 }
	 
	 @GetMapping("/superadminhome")
	 public String showDonorHome(HttpSession session, Model model) {
	     Long userId = (Long) session.getAttribute("userId");
	     System.out.println("Retrieved userId from session: " + userId); // Debug statement
	     if (userId != null) {
	    	 Superadmin user = superadminService.findById(userId);
	         System.out.println("Retrieved user from database: " + user);
	         if (user != null) {
	             model.addAttribute("user", user);
	             model.addAttribute("userId", userId);
	             return "superadminhome";
	         } else {
	             return "redirect:/superadminlogin";
	         }
	     }
	     return "redirect:/superadminlogin";
	 }
	 
	 @GetMapping("/superadminlogout")
	    public String logout(HttpSession session) {
	        session.invalidate(); // Invalidate the session to log out the user
	        return "redirect:/superadminlogin"; // Redirect to the login page or home page
	    }
	 
	 @GetMapping("/alladminusers")
	    public String getAllAdminUsers(Model model, HttpSession session, @RequestParam(defaultValue = "0") int page) {
	        Long userId = (Long) session.getAttribute("userId");
	        if (userId != null) {
	        	 Superadmin user = superadminService.findById(userId);     
	            if (user != null) {
	                int pageSize = 10;
	                List<User1> users = user1Service.findAll(page, pageSize);
	                long totalUsers = user1Service.countAllUsers();
	                int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

	                model.addAttribute("users", users);
	                model.addAttribute("currentPage", page);
	                model.addAttribute("totalPages", totalPages);
	                model.addAttribute("pageSize", pageSize);

	                return "admindataforsuperuser";
	            } else {
	                model.addAttribute("error", "User not found.");
	                return "redirect:/superadminlogin";
	            }
	        } else {
	            model.addAttribute("error", "No user logged in.");
	            return "redirect:/superadminlogin";
	        }
	    }
	 
	
	 
	 @GetMapping("/alldonorsusers")
	    public String getAllDonorUsers(Model model,HttpSession session, @RequestParam(defaultValue = "0") int page) {
		 Long userId = (Long) session.getAttribute("userId");
	        if (userId != null) {
	        	 Superadmin user = superadminService.findById(userId);     
	            if (user != null) {
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

	        return "donordataforsuperuser";
	            } else {
	                model.addAttribute("error", "User not found.");
	                return "redirect:/superadminlogin";
	            }
	        } else {
	            model.addAttribute("error", "No user logged in.");
	            return "redirect:/superadminlogin";
	        }
	    }
	 
	 @GetMapping("/deleteuser/{id}")
	  public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	      userService.deleteUserById(id);
	      redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
	      return "redirect:/alldonorsusers";
	  }
	 
	 @GetMapping("/deleteadminuser/{id}")
	  public String deleteadminuser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	      user1Service.deleteUserById(id);
	      redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
	      return "redirect:/alladminusers";
	  }
	 
	 @GetMapping("/superadminregister")
	    public String showRegistrationForm(Model model) {
	        model.addAttribute("user", new User1());
	        return "superadminregister"; // This will render register.html
	    }
	 
	 @PostMapping("/superadminregister")
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
	        String otp = superadminotpService.generateOtp(user.getMobile());
	        sendOtpToMobile(user.getMobile(), otp);
	        emailService.sendOtp(user.getEmailid(), otp);
	        
	        // Store user in session for later use
	        model.addAttribute("mobile", user.getMobile());
	        model.addAttribute("message", "OTP sent to your mobile number. Please verify.");

	        return "EnterOtp"; // Redirect to OTP verification page
	    }

	    private void sendOtpToMobile(String mobile, String otp) {
	        // Implement SMS sending logic here
	        System.out.println("Sending OTP " + otp + " to mobile " + mobile);
	    }
	    
	    @PostMapping("/adminvalidateuserOtp")
	    public String validateUserOtp(@RequestParam String mobile, HttpSession session, Model model) {
	    	 Long userId = (Long) session.getAttribute("userId");
		        if (userId != null) {
		        	 Superadmin user = superadminService.findById(userId);     
		            if (user != null) {
	            session.setAttribute("userId", user.getId());
	            System.out.println("User ID set in session: " + user.getId()); // Debug statement
	            session.setAttribute("userName", user.getName());
	            return "redirect:/superadminhome";
		            } else {
		                model.addAttribute("error", "User not found.");
		                return "redirect:/superadminlogin";
		            }
		        } else {
		            model.addAttribute("error", "No user logged in.");
		            return "redirect:/superadminlogin";
		        }
		    }
}

