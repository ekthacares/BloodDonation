package com.example.ekthacare.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.BloodRequest;
import com.example.ekthacare.repo.BloodRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    
    
    
}