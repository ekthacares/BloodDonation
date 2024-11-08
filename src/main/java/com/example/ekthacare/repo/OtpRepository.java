package com.example.ekthacare.repo;

import org.springframework.data.repository.CrudRepository;

import com.example.ekthacare.entity.Otp;
import com.example.ekthacare.entity.SuperadminOtp;

public interface OtpRepository extends CrudRepository<Otp, Long> {
	Otp findByMobile(String mobile);
}