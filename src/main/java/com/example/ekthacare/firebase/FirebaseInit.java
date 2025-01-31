package com.example.ekthacare.firebase;


import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInit {
    public static void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/ekthacares-7e31a-firebase-adminsdk-fbsvc-6df3ddea69.json");
            // Set Firebase options
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Initialize Firebase only if it's not already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error initializing Firebase: " + e.getMessage());
        }
    }   
    
}