package com.example.ekthacare.services;

import com.example.ekthacare.entity.CampaignAttendance;
import com.example.ekthacare.repo.CampaignAttendanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CampaignAttendanceService {

    @Autowired
    private CampaignAttendanceRepository repo;

    // -------------------- SAVE OR RE-ACTIVATE --------------------
    public CampaignAttendance saveAttendance(CampaignAttendance request) {

        Optional<CampaignAttendance> existingOpt =
                repo.findByUserIdAndCampaignId(request.getUserId(), request.getCampaignId());

        if (existingOpt.isPresent()) {
            CampaignAttendance existing = existingOpt.get();

            // Reactivate if previously inactive
            existing.setActive(true);
            existing.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
            existing.setMessage(request.getMessage());

            return repo.save(existing);
        }

        // First-time save
        request.setActive(true);
        return repo.save(request);
    }

    // -------------------- UPDATE ACTIVE STATUS --------------------
    public void updateActiveStatus(Long userId, Long campaignId, boolean active) {

        Optional<CampaignAttendance> existingOpt =
                repo.findByUserIdAndCampaignId(userId, campaignId);

        if (existingOpt.isPresent()) {
            CampaignAttendance existing = existingOpt.get();
            existing.setActive(active);
            repo.save(existing);
        }
    }

    // -------------------- CHECK ACTIVE STATUS --------------------
    public boolean isUserActive(Long userId, Long campaignId) {
        return repo.isUserActiveInCampaign(userId, campaignId);
    }

    // -------------------- EXISTING METHODS --------------------
    public CampaignAttendance save(CampaignAttendance attendance) {
        return repo.save(attendance);
    }

    public Page<CampaignAttendance> findAllPaged(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Optional<CampaignAttendance> findById(Long id) {
        return repo.findById(id);
    }
}
