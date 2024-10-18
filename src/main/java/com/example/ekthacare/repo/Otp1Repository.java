package com.example.ekthacare.repo;

import org.springframework.data.repository.CrudRepository;

import com.example.ekthacare.entity.Otp1;

public interface Otp1Repository extends CrudRepository<Otp1, Long> {
    Otp1 findByMobile(String mobile);
}