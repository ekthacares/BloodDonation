package com.example.ekthacare.services;


import com.example.ekthacare.entity.CampaignAttendance;
import com.example.ekthacare.repo.CampaignAttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CampaignAttendanceService {

    @Autowired
    private CampaignAttendanceRepository repo;

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
