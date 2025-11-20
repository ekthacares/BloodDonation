package com.example.ekthacare.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ekthacare.entity.CampaignAttendance;

public interface CampaignAttendanceRepository extends JpaRepository<CampaignAttendance, Long> {

    Optional<CampaignAttendance> findByUserIdAndCampaignId(Long userId, Long campaignId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
           "FROM CampaignAttendance c WHERE c.userId = :userId AND c.campaignId = :campaignId AND c.active = true")
    boolean isUserActiveInCampaign(Long userId, Long campaignId);
}
