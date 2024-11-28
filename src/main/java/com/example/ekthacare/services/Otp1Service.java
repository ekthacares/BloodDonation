package com.example.ekthacare.services;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.Otp;
import com.example.ekthacare.entity.Otp1;
import com.example.ekthacare.repo.Otp1Repository;

@Service
public class Otp1Service {
	
	   @Autowired
	    private Otp1Repository Otp1Repository;
	   
	private Random random = new Random();

    public String generateOtp(String mobile) {
        String otp = String.valueOf(100000 + random.nextInt(900000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(2);

        Otp1 otpEntity = Otp1Repository.findByMobile(mobile);
        if (otpEntity == null) {
            otpEntity = new Otp1();
            otpEntity.setMobile(mobile);
        }
        otpEntity.setOtp(otp);
        otpEntity.setExpirytime(expiryTime);
        Otp1Repository.save(otpEntity);

        return otp;
    }

    public boolean validateOtp(String mobile, String otp) {
        Otp1 otpEntity = Otp1Repository.findByMobile(mobile);
        return otpEntity != null && otpEntity.getOtp().equals(otp) && otpEntity.getExpirytime().isAfter(LocalDateTime.now());
    }
    
    public boolean isOtpExpired(String mobile) {
        Otp1 otpEntity = Otp1Repository.findByMobile(mobile);
        if (otpEntity == null) {
            return true; // Consider expired if no OTP exists
        }
        return otpEntity.getExpirytime().isBefore(LocalDateTime.now()); // Expired if expiry time has passed
    }
}