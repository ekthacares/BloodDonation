package com.example.ekthacare.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ekthacare.entity.CampaignAttendance;

public interface CampaignAttendanceRepository extends JpaRepository<CampaignAttendance, Long> {

    Optional<CampaignAttendance> findByUserIdAndCampaignId(Long userId, Long campaignId);

    void deleteByUserIdAndCampaignId(Long userId, Long campaignId);

    boolean existsByUserIdAndCampaignId(Long userId, Long campaignId);
}
