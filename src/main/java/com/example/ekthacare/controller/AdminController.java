package com.example.ekthacare.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ekthacare.entity.BloodDonation;
import com.example.ekthacare.entity.BloodRequest;
import com.example.ekthacare.entity.SearchRequest;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.User1Repository;
import com.example.ekthacare.services.BloodDonationService;
import com.example.ekthacare.services.BloodRequestService;
import com.example.ekthacare.services.EmailService;
import com.example.ekthacare.services.Otp1Service;
import com.example.ekthacare.services.SearchRequestService;
import com.example.ekthacare.services.SmsService;
import com.example.ekthacare.services.User1Service;
import com.example.ekthacare.services.UserService;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Cell;
import jakarta.servlet.http.HttpSession;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;



@Controller
public class AdminController {
	
	 @Autowired
	    private User1Repository user1Repository;
	 
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
	    private BloodDonationService donationService;
	  
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
	             return "redirect:/adminlogin";
	         }
	     }
	     return "redirect:/adminlogin";
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
	                model.addAttribute("message", "User not found.");
	                return "redirect:/adminlogin";
	            }
	        } else {
	            model.addAttribute("message", "No user logged in.");
	            return "redirect:/adminlogin";
	        }
	    }
	    
	    @GetMapping("/logout1")
	    public String logout(HttpSession session) {
	        session.invalidate(); // Invalidate the session to log out the user
	        return "redirect:/adminlogin"; // Redirect to the login page or home page
	    }
	    
	    @GetMapping("/alladmindata")
	    public String getAllUsers(Model model, HttpSession session, @RequestParam(defaultValue = "0") int page) {
	        Long userId = (Long) session.getAttribute("userId");
	        if (userId != null) {
	            User1 user = user1Service.findById(userId);	            
	            if (user != null) {
	                int pageSize = 10;
	                List<User1> users = user1Service.findAll(page, pageSize);
	                long totalUsers = user1Service.countAllUsers();
	                int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

	                model.addAttribute("users", users);
	                model.addAttribute("currentPage", page);
	                model.addAttribute("totalPages", totalPages);
	                model.addAttribute("pageSize", pageSize);

	                return "alladmindata";
	            } else {
	                model.addAttribute("error", "User not found.");
	                return "redirect:/adminlogin";
	            }
	        } else {
	            model.addAttribute("error", "No user logged in.");
	            return "redirect:/adminlogin";
	        }
	    }


	    
	    @GetMapping("/viewbloodrequestdata")
	    public String viewRequests(Model model, @RequestParam(defaultValue = "0") int page) {
	        int pageSize = 10; // Define the number of requests per page

	        // Fetch paginated list of blood requests
	        List<BloodRequest> bloodRequests = bloodRequestService.getAllBloodRequests(page, pageSize);
	        long totalRequests = bloodRequestService.countAllRequests();
	        int totalPages = (int) Math.ceil((double) totalRequests / pageSize);

	        // Add necessary attributes to the model
	        model.addAttribute("bloodRequests", bloodRequests); // List of blood requests
	        model.addAttribute("currentPage", page);            // Current page number
	        model.addAttribute("totalPages", totalPages);       // Total number of pages
	        model.addAttribute("pageSize", pageSize);           // Page size

	        System.out.println("bloodRequests data is " + bloodRequests);
	        return "viewbloodrequestdata"; // Return the Thymeleaf template for the request list
	    }

	    

	    @GetMapping("/allsearchrequestdata")
	    public String viewAllSearchRequests(
	            @RequestParam(required = false) String filter, 
	            Model model, 
	            @RequestParam(defaultValue = "0") int page) {

	        int pageSize = 10;
	        List<SearchRequest> searchRequests;
	        long totalRequests;

	        // Apply filtering based on the 'filter' parameter
	        if ("week".equals(filter)) {
	            searchRequests = searchRequestService.findByTimestampThisWeek(page, pageSize);
	            totalRequests = searchRequestService.countRequestsThisWeek();
	        } else if ("month".equals(filter)) {
	            searchRequests = searchRequestService.findByTimestampThisMonth(page, pageSize);
	            totalRequests = searchRequestService.countRequestsThisMonth();
	        } else if ("last3months".equals(filter)) {
	            searchRequests = searchRequestService.findByTimestampLast3Months(page, pageSize);
	            totalRequests = searchRequestService.countRequestsLast3Months();
	        } else {
	            // Default case, get all search requests
	            searchRequests = searchRequestService.findAll(page, pageSize);
	            totalRequests = searchRequestService.countAllRequests();
	        }

	        // Calculate total pages for pagination
	        int totalPages = (int) Math.ceil((double) totalRequests / pageSize);

	        // Add attributes to the model
	        model.addAttribute("searchRequests", searchRequests);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", totalPages);
	        model.addAttribute("filter", filter); // Retain the current filter for UI

	        return "allsearchrequestdata";  // Name of the template
	    }


	    
	    
	   
	  @GetMapping("/user/{userId}")
	   public String viewUserProfile(@PathVariable("userId") Long userId, Model model) {
	      User user = userService.findById(userId);
	       model.addAttribute("user", user);
	     return "userDetails"; // Name of the Thymeleaf template to display user profile
	       // return "fragments/userprofile :: userProfileFragment";
	   }
	  
	  @GetMapping("/deleteUser/{id}")
	  public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	      userService.deleteUserById(id);
	      redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
	      return "redirect:/alldonorsdata";
	  }
	   
	  @GetMapping("/api/user")
	  @ResponseBody
	  public User getUser(@RequestParam Long id) {
	      return userService.findById(id);
	  }
	  
	  
	  @PostMapping("/updateProfile1")
	    public String updateProfile(@ModelAttribute User1 user, RedirectAttributes redirectAttributes) {
	        // Save change logs here if necessary
	        user1Service.updateUser1(user); // Your service method to update user data
	        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
	        return "redirect:/adminviewprofile"; // Redirect back to the profile page
	    }
	  
	  
		
		/*
		 * @GetMapping("/donations/{userId}") public String
		 * viewUserDonations(@PathVariable("userId") Long userId, Model model) {
		 * BloodDonation donations = donationService.findDonationsByUserId(userId);
		 * model.addAttribute("donations", donations); model.addAttribute("userId",
		 * userId); return "redirect:/alldonationlist"; // This refers to a separate
		 * Thymeleaf template "userDonations.html"
		 * 
		 * }
		 */
	  
	  
	 
	  
	  @GetMapping("/donations/{recipientId}")
	  public String viewUserDonations(@PathVariable("recipientId") Long recipientId, Model model) {
	      // Fetch donations for the recipient
	      List<BloodDonation> donations = donationService.findDonationsByRecipientId(recipientId); // Ensure this method returns a List

	      // Check if donations exist
	      if (donations != null && !donations.isEmpty()) {
	          model.addAttribute("donations", donations);
	      } else {
	          model.addAttribute("message", "No donations found for this recipient.");
	      }
	      
	     
	      model.addAttribute("recipientId", recipientId);
	      return "alldonationlist"; // Ensure this is the name of your Thymeleaf template
	  }


	  @GetMapping("/adminusers/exceldownload")
	    public ResponseEntity<byte[]> downloadUserData() throws IOException {
	        List<User1> users = user1Service.getAllUsers(); // Fetch all users from database

	        // Generate CSV
	        StringBuilder csvBuilder = new StringBuilder();
	        csvBuilder.append("ID,Name,Mobile,Email,Date of Birth,Blood Group,Age,Gender,Address,City,State\n");
	        for (User1 user : users) {
	            csvBuilder.append(user.getId()).append(",")
	                      .append(user.getName()).append(",")
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
	        headers.add("Content-Disposition", "attachment; filename=adminusers.csv");
	        headers.add("Content-Type", "text/csv");

	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(csvData);
	    }
	    
	    @GetMapping("/adminusers/pdfdownload")
	    public ResponseEntity<byte[]> downloadUserDataAsPdf() throws IOException {
	        List<User1> users = user1Service.getAllUsers(); // Fetch all users from the database

	        // Create a byte array output stream to hold PDF data
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	        // Initialize PDF document with A4 page size
	        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf, PageSize.A4.rotate()); // Use A4 page size

	        // Add a title
	        Paragraph title = new Paragraph("Admin Users Data")
	            .setTextAlignment(TextAlignment.CENTER)
	            .setFontSize(18)
	            .setBold();
	        document.add(title);

	        // Create a table with appropriate column widths
	        Table table = new Table(new float[]{1, 2, 2, 3, 2, 2, 1, 1, 3, 2, 2});
	        table.setWidth(UnitValue.createPercentValue(100));

	        // Add table headers
	        String[] headers = {"ID", "Name", "Mobile", "Email", "Date of Birth", "Blood Group", "Age", "Gender", "Address", "City", "State"};
	        for (String header : headers) {
	            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY)));
	        }

	        // Add user data rows
	        for (User1 user : users) {
	            table.addCell(new Cell().add(new Paragraph(String.valueOf(user.getId()))));
	            table.addCell(new Cell().add(new Paragraph(user.getName())));
	            table.addCell(new Cell().add(new Paragraph(user.getMobile())));
	            table.addCell(new Cell().add(new Paragraph(user.getEmailid())));
	            table.addCell(new Cell().add(new Paragraph(user.getDateofbirth().toString())));
	            table.addCell(new Cell().add(new Paragraph(user.getBloodgroup())));
	            table.addCell(new Cell().add(new Paragraph(String.valueOf(user.getAge()))));
	            table.addCell(new Cell().add(new Paragraph(user.getGender())));
	            table.addCell(new Cell().add(new Paragraph(user.getAddress())));
	            table.addCell(new Cell().add(new Paragraph(user.getCity())));
	            table.addCell(new Cell().add(new Paragraph(user.getState())));
	        }

	        document.add(table);
	        document.close();

	        // Prepare the PDF for download
	        byte[] pdfData = byteArrayOutputStream.toByteArray();
	        HttpHeaders headers1 = new HttpHeaders();
	        headers1.add("Content-Disposition", "attachment; filename=adminusers.pdf");
	        headers1.add("Content-Type", "application/pdf");

	        return ResponseEntity.ok()
	                .headers(headers1)
	                .body(pdfData);
	    }

	    
	  }

	
