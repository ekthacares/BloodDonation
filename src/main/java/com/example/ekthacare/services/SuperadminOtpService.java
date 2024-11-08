package com.example.ekthacare.services;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.Otp;
import com.example.ekthacare.entity.SuperadminOtp;
import com.example.ekthacare.repo.OtpRepository;
import com.example.ekthacare.repo.SuperOtpRepository;

@Service
public class SuperadminOtpService {
	
	 @Autowired
	    private SuperOtpRepository superotpRepository;

	    private Random random = new Random();

	    public String generateOtp(String mobile) {
	        String otp = String.valueOf(100000 + random.nextInt(900000));
	        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

	        SuperadminOtp otpEntity = superotpRepository.findByMobile(mobile);
	        if (otpEntity == null) {
	            otpEntity = new SuperadminOtp();
	            otpEntity.setMobile(mobile);
	        }
	        otpEntity.setOtp(otp);
	        otpEntity.setExpirytime(expiryTime);
	        superotpRepository.save(otpEntity);

	        return otp;
	    }

	    public boolean validateOtp(String mobile, String otp) {
	        SuperadminOtp otpEntity = superotpRepository.findByMobile(mobile);
	        return otpEntity != null && otpEntity.getOtp().equals(otp) && otpEntity.getExpirytime().isAfter(LocalDateTime.now());
	    }
	}


