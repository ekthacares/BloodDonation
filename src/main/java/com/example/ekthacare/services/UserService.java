package com.example.ekthacare.services;



import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.ekthacare.entity.ChangeLog;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.entity.User1;
import com.example.ekthacare.repo.ChangeLogRepository;
import com.example.ekthacare.repo.UserRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChangeLogRepository changeLogRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }
    
   
    
   
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
        
    
 
    
    public List<User> findByBloodgroupAndCityAndState(String bloodgroup, String city, String state) {
        return userRepository.findByBloodgroupAndCityAndState(bloodgroup, city, state);
    }

    public List<String> getAllCities() {
        return ((Collection<User>) userRepository.findAll()).stream().map(User::getCity).distinct().collect(Collectors.toList());
    }

    public List<String> getAllStates() {
        return ((Collection<User>) userRepository.findAll()).stream().map(User::getState).distinct().collect(Collectors.toList());
    }

	

	public User findByDonorname(String donorname) {
		// TODO Auto-generated method stub
		return userRepository.findByDonorname(donorname);
	}

	public User getUserById(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<User> getUsersByIds(Set<Long> userIds) {
	    return userRepository.findAllById(userIds);
	}
	
	
	public User saveUser(User user) {
        if (userRepository.existsByMobile(user.getMobile())) {
            throw new IllegalArgumentException("Mobile number already exists");
        }
        return userRepository.save(user);
    }

	public void updateUser(User user) {
	    User existingUser = userRepository.findById(user.getId()).orElseThrow();
	    // Check for changes and save to change log
	    if (!existingUser.getDonorname().equals(user.getDonorname())) {
	        saveChangeLog(user.getId(), "donorname", existingUser.getDonorname(), user.getDonorname());
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
       
	    userRepository.save(user);
	}

	private void saveChangeLog(Long userId, String changedField, String oldValue, String newValue) {
	    ChangeLog log = new ChangeLog();
	    log.setUserId(userId);
	    log.setChangedField(changedField);
	    log.setOldValue(oldValue);
	    log.setNewValue(newValue);
	    log.setChangedAt(LocalDateTime.now());
	    changeLogRepository.save(log); // Assuming you have a repository for ChangeLog
	}
	

	public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {  // Check if the user exists
            userRepository.deleteById(id);   // Delete user if exists
        }
    }
	
	public List<User> getAllUsers(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize); // Create a PageRequest with the specified page and size
        Page<User> pagedResult = userRepository.findAll(pageRequest); // Fetch paginated users
        return pagedResult.getContent(); // Return the list of users
    }

    public long countAllRequests() {
        return userRepository.count(); // Count the total number of users
    }
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Transactional
    public void saveUsers(List<User> users) {
        for (User user : users) {
            User existingUser = userRepository.findByMobile(user.getMobile());
            if (existingUser != null) {  // Check if existingUser is null
                logger.warn("User with mobile {} already exists. Skipping.", user.getMobile());
                continue; // Skip existing users
            }
            userRepository.save(user);
        }
    }
    
    public boolean userExists(String mobile) {
        return userRepository.findByMobile(mobile).isPresent(); // Check if a user with the mobile number exists
    }
}
