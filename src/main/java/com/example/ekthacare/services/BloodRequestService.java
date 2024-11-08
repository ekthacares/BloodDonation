package com.example.ekthacare.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.BloodRequest;
import com.example.ekthacare.repo.BloodRequestRepository;

import java.util.List;

@Service
public class BloodRequestService {
    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    public BloodRequest saveBloodRequest(BloodRequest bloodRequest) {
        return bloodRequestRepository.save(bloodRequest);
    }

    public List<BloodRequest> getAllBloodRequests() {
        return (List<BloodRequest>) bloodRequestRepository.findAll();
    }
    
    public List<BloodRequest> getAllBloodRequests(int page, int pageSize) {
        Page<BloodRequest> requestPage = bloodRequestRepository.findAll(PageRequest.of(page, pageSize));
        return requestPage.getContent(); // Return the content of the page
    }

    // Count total blood requests
    public long countAllRequests() {
        return bloodRequestRepository.count();
    }
    
}