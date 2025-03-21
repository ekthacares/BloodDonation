package com.example.ekthacare.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ekthacare.entity.ChangeLog;
import com.example.ekthacare.entity.User1;

public interface User1Repository extends JpaRepository<User1, Long> {
	User1 findByMobile(String mobile);
	
	 boolean existsByMobile(String mobile);

	void save(ChangeLog log);

	Optional<User1> findById(Long id);
	
	
}
