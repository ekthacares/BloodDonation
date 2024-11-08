package com.example.ekthacare.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.Superadmin;
import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.SuperAdminRepository;

@Service
@Component
public class SuperAdminService {
	
	 @Autowired
	    private SuperAdminRepository superadminRepository;
	
	

	public Superadmin findByMobile(String mobile) {
		
		return superadminRepository.findByMobile(mobile);
	}



	
	public Superadmin findById(Long id) {
        return superadminRepository.findById(id).orElse(null);
    }
}
