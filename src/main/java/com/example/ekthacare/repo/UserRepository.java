package com.example.ekthacare.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ekthacare.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByMobile(String mobile);
	
	List<User> findByBloodgroupAndCityAndState(String bloodgroup, String city, String state);
	
	List<User> findByBloodgroupAndAreaAndCityAndState(
	        String bloodgroup,
	        String area,
	        String city,
	        String state
	);


	User findByDonorname(String donorname);
	
	 boolean existsByMobile(String mobile);
	
	  List<User> findAllById(Iterable<Long> ids);
	  
	  List<User> findByMobileIn(List<String> mobile);
	  
	  @Query("SELECT u.fcmToken FROM User u WHERE u.id = :id")
	    String findFcmTokenById(Long id);
	
}

