package com.example.ekthacare.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.User;
import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.User1Repository;


@Service
@Component
public class User1Service {

    @Autowired
    private User1Repository user1Repository;

    public User1 save(User1 user) {
        return user1Repository.save(user);
    }

    public User1 findByMobile(String mobile) {
        return user1Repository.findByMobile(mobile);
    }

    public User1 findById(Long id) {
        return user1Repository.findById(id).orElse(null);
    }
    
    public List<User1> getAllUsers() {
        return (List<User1>) user1Repository.findAll();
    }
    
    public User1 saveUser(User1 user) {
        if (user1Repository.existsByMobile(user.getMobile())) {
            throw new IllegalArgumentException("Mobile number already exists");
        }
        return user1Repository.save(user);
    }
}

