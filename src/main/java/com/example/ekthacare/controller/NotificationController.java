package com.example.ekthacare.controller;


import com.example.ekthacare.entity.NotificationRequest;
import com.example.ekthacare.repo.NotificationRequestRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRequestRepository notificationRepository; // Inject repository

    @PostMapping("/send")
    public ResponseEntity<?> sendPushNotification(@RequestBody NotificationRequest notificationRequest) {
        try {
            // Save notification to database
            notificationRequest.setSentTime(LocalDateTime.now());
            notificationRepository.save(notificationRequest);

            // Create and send FCM notification
            Notification notification = Notification.builder()
                    .setTitle(notificationRequest.getTitle())
                    .setBody(notificationRequest.getMessage())
                    .build();

            Message message = Message.builder()
                    .setToken(notificationRequest.getFcmToken())
                    .setNotification(notification)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);

            return ResponseEntity.ok("Notification sent successfully. Response: " + response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending notification: " + e.getMessage());
        }
    }

    // Endpoint to fetch all notifications
    @GetMapping("/all")
    public ResponseEntity<List<NotificationRequest>> getAllNotifications() {
        List<NotificationRequest> notifications = notificationRepository.findAll();
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<NotificationRequest>> getUserNotifications(@RequestParam String fcmToken) {
        List<NotificationRequest> notifications = notificationRepository.findByFcmToken(fcmToken);
        return ResponseEntity.ok(notifications);
    }
}







