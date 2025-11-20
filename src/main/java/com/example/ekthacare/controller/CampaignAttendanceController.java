package com.example.ekthacare.controller;

import com.example.ekthacare.entity.CampaignAttendance;
import com.example.ekthacare.services.CampaignAttendanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class CampaignAttendanceController {

    @Autowired
    private CampaignAttendanceService service;

    // -------------------- SAVE or RE-ACTIVATE --------------------
    @PostMapping("/save")
    public ResponseEntity<?> saveAttendance(@RequestBody CampaignAttendance attendance) {
        return ResponseEntity.ok(service.saveAttendance(attendance));
    }

    // -------------------- UPDATE ACTIVE STATUS (UNCHECK) --------------------
    @PutMapping("/update")
    public ResponseEntity<?> updateActiveStatus(
            @RequestParam Long userId,
            @RequestParam Long campaignId,
            @RequestParam boolean active) {

        service.updateActiveStatus(userId, campaignId, active);
        return ResponseEntity.ok("Status updated");
    }

    // -------------------- CHECK IF USER IS ACTIVE --------------------
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAttendance(
            @RequestParam Long userId,
            @RequestParam Long campaignId) {

        return ResponseEntity.ok(service.isUserActive(userId, campaignId));
    }
}
