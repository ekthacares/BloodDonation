package com.example.ekthacare.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.OtpVerification;
import com.example.ekthacare.repo.OtpVerificationRepository;

@Service
public class OtpVerificationService {

	@Autowired
    private OtpVerificationRepository otpVerificationRepository;

    // Method to generate and save OTP
    public String generateAndSaveOtp(Long recipientId, Long loggedInUserId) {
        String otp = generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);  // OTP expires in 5 minutes

        // Create and save the OTP verification entry
        OtpVerification otpVerification = new OtpVerification(recipientId, loggedInUserId, otp, expirationTime);
        otpVerificationRepository.save(otpVerification);
        
        return otp;  // Return the generated OTP (which can be sent via email)
    }

    // Validate OTP
    public boolean validateOtp(Long recipientId, Long loggedInUserId, String otp) {
        Optional<OtpVerification> otpVerificationOpt = otpVerificationRepository.findTopByRecipientIdAndLoggedInUserIdOrderByGeneratedAtDesc(recipientId, loggedInUserId);

        if (otpVerificationOpt.isPresent()) {
            OtpVerification storedOtp = otpVerificationOpt.get();
            // Check if OTP matches and is not expired
            if (storedOtp.getOtp().equals(otp) && storedOtp.getGeneratedAt().plusMinutes(5).isAfter(LocalDateTime.now())) {
                // Do not delete OTP - Keep it for reuse or logging purposes
                return true;
            }
        }
        return false;  // Return false if OTP is invalid or expired
    }

    // Generate a 6-digit OTP
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Random 6-digit number
        return String.valueOf(otp);  // Return OTP as string
    }
}
