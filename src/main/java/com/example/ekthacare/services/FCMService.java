package com.example.ekthacare.services;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FCMService {

	public void sendPushNotification(List<String> tokens, String title, String message, String type) {
	    System.out.println("🔥 FCMService.sendPushNotification() called with tokens: " + tokens);

	    if (tokens == null || tokens.isEmpty()) {
	        System.out.println("⚠️ No FCM tokens provided. Skipping push notification.");
	        return;
	    }

	    // Create a multicast message with a data payload
	    MulticastMessage multicastMessage = MulticastMessage.builder()
	            .addAllTokens(tokens)
//	            .setNotification(Notification.builder()
//	                    .setTitle(title)
//	                    .setBody(message)
//	                    .build())
	            .putData("type", type) // ✅ Add "type" field ("campaign" or "notification")
	            .putData("title", title) 
	            .putData("body", message)
	            .build();

	    try {
	        System.out.println("📡 Sending push notifications to FCM...");
	        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);

	        System.out.println("✅ Push notifications sent successfully.");
	        System.out.println("📊 Success count: " + response.getSuccessCount());
	        System.out.println("📉 Failure count: " + response.getFailureCount());

	        if (response.getFailureCount() > 0) {
	            response.getResponses().forEach(resp -> {
	                if (!resp.isSuccessful()) {
	                    System.out.println("❌ FCM Error: " + resp.getException().getMessage());
	                }
	            });
	        }
	    } catch (FirebaseMessagingException e) {
	        System.out.println("❌ Error sending push notification: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

}
