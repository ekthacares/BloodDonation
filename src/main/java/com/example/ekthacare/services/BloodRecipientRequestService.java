package com.example.ekthacare.services;

import com.example.ekthacare.entity.BloodRecipientRequest;
import com.example.ekthacare.repo.BloodRecipientRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BloodRecipientRequestService {

    private final BloodRecipientRequestRepository repo;

    public BloodRecipientRequestService(BloodRecipientRequestRepository repo) {
        this.repo = repo;
    }

    public void saveBloodRecipientRequest(
            Long userId,
            String bloodgroup,
            String hospitalName,
            String city,
            String state,
            String area,
            String address,
            LocalDate requestedDate,
            int donorsCount,       // <-- Important for your entity
            String message         // optional notes
    ) {

        BloodRecipientRequest req = new BloodRecipientRequest();

        req.setUserId(userId);
        req.setBloodgroup(bloodgroup);
        req.setHospitalName(hospitalName);
        req.setCity(city);
        req.setState(state);
        req.setArea(area);
        req.setAddress(address);
        req.setRequestedDate(requestedDate);
        req.setCreatedAt(LocalDateTime.now());
        req.setDonorsCount(donorsCount);
        req.setMessage(message);

        repo.save(req);
    }
}
