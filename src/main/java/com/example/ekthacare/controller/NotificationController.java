package com.example.ekthacare.controller;


import com.example.ekthacare.entity.NotificationRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/notifications")
public class NotificationController {

    @PostMapping("/send")
    public ResponseEntity<?> sendPushNotification(@RequestBody NotificationRequest notificationRequest) {
        try {
            // Create the notification object using the builder pattern
            Notification notification = Notification.builder()
                    .setTitle(notificationRequest.getTitle()) // Set the title of the notification
                    .setBody(notificationRequest.getMessage()) // Set the message of the notification
                    .build();

            // Create the message with the given FCM token and notification details
            Message message = Message.builder()
                    .setToken(notificationRequest.getFcmToken()) // FCM token of the device
                    .setNotification(notification) // Pass the created notification object
                    .build();

            // Send the message via Firebase Cloud Messaging (FCM)
            String response = FirebaseMessaging.getInstance().send(message);

            // Return success response
            return ResponseEntity.ok("Notification sent successfully. Response: " + response);

        } catch (Exception e) {
            // Return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending notification: " + e.getMessage());
        }
    }
}
