package com.example.ekthacare.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.ekthacare.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByMobile(String mobile);
	
	List<User> findByBloodgroupAndCityAndState(String bloodgroup, String city, String state);

	User findByDonorname(String donorname);
	
	 boolean existsByMobile(String mobile);
	
	  List<User> findAllById(Iterable<Long> ids);
	  
	  
	
}
