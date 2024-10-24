package com.example.ekthacare.repo;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.example.ekthacare.entity.ChangeLog;
import com.example.ekthacare.entity.User1;

public interface User1Repository extends JpaRepository<User1, Long> {
	User1 findByMobile(String mobile);
	
	 boolean existsByMobile(String mobile);

	void save(ChangeLog log);

    
	
	
}
