package com.example.ekthacare.repo;



import org.springframework.data.repository.CrudRepository;

import com.example.ekthacare.entity.BloodRequest;


public interface BloodRequestRepository extends CrudRepository<BloodRequest, Long> {
  
}