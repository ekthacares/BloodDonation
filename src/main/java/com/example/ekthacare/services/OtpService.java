package com.example.ekthacare.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.Otp;
import com.example.ekthacare.repo.OtpRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    private Random random = new Random();

    public String generateOtp(String mobile) {
        String otp = String.valueOf(100000 + random.nextInt(900000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(2);

        Otp otpEntity = otpRepository.findByMobile(mobile);
        if (otpEntity == null) {
            otpEntity = new Otp();
            otpEntity.setMobile(mobile);
        }
        otpEntity.setOtp(otp);
        otpEntity.setExpirytime(expiryTime);
        otpRepository.save(otpEntity);

        return otp;
    }

    public boolean validateOtp(String mobile, String otp) {
        Otp otpEntity = otpRepository.findByMobile(mobile);
        return otpEntity != null && otpEntity.getOtp().equals(otp) && otpEntity.getExpirytime().isAfter(LocalDateTime.now());
    }
    
    public boolean isOtpExpired(String mobile) {
        Otp otpEntity = otpRepository.findByMobile(mobile);
        if (otpEntity == null) {
            return true; // Consider expired if no OTP exists
        }
        return otpEntity.getExpirytime().isBefore(LocalDateTime.now()); // Expired if expiry time has passed
    }

}
