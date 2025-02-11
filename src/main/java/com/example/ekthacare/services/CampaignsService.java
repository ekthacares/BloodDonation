package com.example.ekthacare.services;

import org.springframework.stereotype.Service;
import com.example.ekthacare.entity.Campaigns;
import com.example.ekthacare.entity.User;
import com.example.ekthacare.repo.CampaignsRepository;
import com.example.ekthacare.repo.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class CampaignsService {

    private final CampaignsRepository campaignsRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    public CampaignsService(CampaignsRepository campaignsRepository, UserRepository userRepository, FCMService fcmService) {
        this.campaignsRepository = campaignsRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    public Campaigns createCampaign(String title, String message, String sendTo, String mobile) {
        System.out.println("📢 Creating campaign with Title: " + title);
        System.out.println("📜 Message: " + message);

        // Save campaign in the database
        Campaigns campaign = new Campaigns();
        campaign.setTitle(title);
        campaign.setMessage(message);
        campaign = campaignsRepository.save(campaign);

        List<String> tokens;

        if ("all".equalsIgnoreCase(sendTo)) {
            // Fetch all FCM tokens from users
            tokens = userRepository.findAll().stream()
                    .map(User::getFcmToken)
                    .filter(token -> token != null && !token.isEmpty())
                    .collect(Collectors.toList());
        } else {
            // Convert comma-separated mobile numbers into a List
            List<String> mobileList = Arrays.stream(mobile.split(","))
                    .map(String::trim) // Remove extra spaces
                    .collect(Collectors.toList());

            // Fetch users by mobile numbers and extract their FCM tokens
            tokens = userRepository.findByMobileIn(mobileList).stream()
                    .map(User::getFcmToken)
                    .filter(token -> token != null && !token.isEmpty())
                    .collect(Collectors.toList());

            System.out.println("📡 Sending notifications to mobiles: " + mobileList);
            System.out.println("🔑 Tokens found: " + tokens);
        }

        // Check if tokens exist
        if (tokens.isEmpty()) {
            System.out.println("⚠️ No valid FCM tokens found! Skipping notification.");
            return campaign;
        }

        // Check if fcmService is properly injected
        if (fcmService == null) {
            System.out.println("🚨 fcmService is NULL! Dependency injection issue.");
            return campaign;
        }

        try {
            System.out.println("🚀 Calling FCMService to send notification...");
            fcmService.sendPushNotification(tokens, title, message);
            System.out.println("✅ FCMService call completed.");
        } catch (Exception e) {
            System.out.println("❌ Error calling FCMService: " + e.getMessage());
            e.printStackTrace();
        }

        return campaign;
    }

    public List<Campaigns> getAllCampaigns() {
        return campaignsRepository.findAll();
    }
}
