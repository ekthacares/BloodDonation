package com.example.ekthacare.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.example.ekthacare.entity.Campaigns;

@Repository
public interface CampaignsRepository extends JpaRepository<Campaigns, Long> {
	
	 @Query("SELECT c FROM Campaigns c ORDER BY c.createdAt DESC")
	    java.util.List<Campaigns> findTop2ByOrderByCreatedAtDesc(Pageable pageable);	
	}


