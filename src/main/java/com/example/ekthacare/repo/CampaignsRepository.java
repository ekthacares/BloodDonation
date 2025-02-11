package com.example.ekthacare.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ekthacare.entity.Campaigns;

@Repository
public interface CampaignsRepository extends JpaRepository<Campaigns, Long> {
}
