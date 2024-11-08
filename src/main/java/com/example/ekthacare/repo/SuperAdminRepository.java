package com.example.ekthacare.repo;


import com.example.ekthacare.entity.Superadmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperAdminRepository extends JpaRepository<Superadmin, Long> {
    // Additional query methods can be defined here if needed
	Superadmin findByMobile(String mobile);
}
