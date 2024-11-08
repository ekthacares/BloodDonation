package com.example.ekthacare.services;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.ChangeLog;
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
    
    public void updateUser1(User1 user) {
	    User1 existingUser = user1Repository.findById(user.getId()).orElseThrow();
	    // Check for changes and save to change log
	    if (!existingUser.getName().equals(user.getName())) {
	        saveChangeLog(user.getId(), "name", existingUser.getName(), user.getName());
	    }
	    if (!existingUser.getMobile().equals(user.getMobile())) {
	        saveChangeLog(user.getId(), "mobile", existingUser.getMobile(), user.getMobile());
	    }  
	    if (!existingUser.getEmailid().equals(user.getEmailid())) {
	        saveChangeLog(user.getId(), "emailid", existingUser.getEmailid(), user.getEmailid());
	    }	   
	    if (!existingUser.getGender().equals(user.getGender())) {
	        saveChangeLog(user.getId(), "gender", existingUser.getGender(), user.getGender());
	    }
	    if (!existingUser.getAddress().equals(user.getAddress())) {
	        saveChangeLog(user.getId(), "address", existingUser.getAddress(), user.getAddress());
	    }
	    if (!existingUser.getCity().equals(user.getCity())) {
	        saveChangeLog(user.getId(), "city", existingUser.getCity(), user.getCity());
	    }
	    if (!existingUser.getState().equals(user.getState())) {
	        saveChangeLog(user.getId(), "state", existingUser.getState(), user.getState());
	    } 
       
	    user1Repository.save(user);
	}

	private void saveChangeLog(Long userId, String changedField, String oldValue, String newValue) {
	    ChangeLog log = new ChangeLog();
	    log.setUserId(userId);
	    log.setChangedField(changedField);
	    log.setOldValue(oldValue);
	    log.setNewValue(newValue);
	    log.setChangedAt(LocalDateTime.now());
	    user1Repository.save(log); // Assuming you have a repository for ChangeLog
	}

	public List<User1> findAll(int page, int pageSize) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        Page<User1> usersPage = user1Repository.findAll(pageable);
        return usersPage.getContent();
    }

    // Count total number of users
    public long countAllUsers() {
        return user1Repository.count();
    }
	
    public void deleteUserById(Long id) {
        if (user1Repository.existsById(id)) {  // Check if the user exists
            user1Repository.deleteById(id);   // Delete user if exists
        }
    }
}

