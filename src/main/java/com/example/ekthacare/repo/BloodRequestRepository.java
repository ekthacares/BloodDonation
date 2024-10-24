package com.example.ekthacare.repo;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.example.ekthacare.entity.BloodRequest;


public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
  
}