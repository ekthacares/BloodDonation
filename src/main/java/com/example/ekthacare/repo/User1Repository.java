package com.example.ekthacare.repo;

import org.springframework.data.repository.CrudRepository;

import com.example.ekthacare.entity.ChangeLog;
import com.example.ekthacare.entity.User1;

public interface User1Repository extends CrudRepository<User1, Long> {
	User1 findByMobile(String mobile);
	
	 boolean existsByMobile(String mobile);

	void save(ChangeLog log);
	
}
