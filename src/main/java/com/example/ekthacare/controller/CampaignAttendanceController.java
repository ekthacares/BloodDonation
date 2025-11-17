package com.example.ekthacare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import com.example.ekthacare.entity.CampaignAttendance;
import com.example.ekthacare.repo.CampaignAttendanceRepository;

@RestController
@RequestMapping("/api/attendance")
public class CampaignAttendanceController {

    @Autowired
    private CampaignAttendanceRepository repo;

    @PostMapping("/save")
    public ResponseEntity<?> saveAttendance(@RequestBody CampaignAttendance attendance) {
        return ResponseEntity.ok(repo.save(attendance));
    }

    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAttendance(
            @RequestParam Long userId,
            @RequestParam Long campaignId) {

        repo.deleteByUserIdAndCampaignId(userId, campaignId);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAttendance(
            @RequestParam Long userId,
            @RequestParam Long campaignId) {

        return ResponseEntity.ok(repo.existsByUserIdAndCampaignId(userId, campaignId));
    }
}
