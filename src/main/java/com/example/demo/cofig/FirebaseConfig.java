package com.example.demo.cofig;

import java.io.FileInputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

  @Bean
   DatabaseReference userDatabaseReference() {
    initializeFirebase();
    return FirebaseDatabase.getInstance().getReference("PushNotifications");
  }

  @PostConstruct
  private void initializeFirebase() {
    try {
      String secretRealtimeDBString = "https://my-firebase-with-notifications-default-rtdb.firebaseio.com/";
      FileInputStream serviceAccount = new FileInputStream("./serviceAccountKey.json");
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .setDatabaseUrl(secretRealtimeDBString)
          .build();

      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Failed to initialize Firebase", e);
    }
  }
}
