package com.example.ekthacare.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.example.ekthacare.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByMobile(String mobile);
	
	List<User> findByBloodgroupAndCityAndState(String bloodgroup, String city, String state);

	User findByDonorname(String donorname);
	
	 boolean existsByMobile(String mobile);
	
	  List<User> findAllById(Iterable<Long> ids);
	  
	  
	
}

