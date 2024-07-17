package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.example.demo.model.NotificationRequest;

@Service
public class PushNotificationService {

  @Autowired
  private DatabaseReference databaseReference;

  public ResponseEntity<String> sendNotificationToAll(String title, String body) {
    Message message = Message.builder()
        .setNotification(Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build())
        .setTopic("all")
        .build();

    try {
      // Send the message via FCM
      String response = FirebaseMessaging.getInstance().send(message);
      System.out.println("Successfully sent message: " + response);

      // Save notification details to Firebase Realtime Database
      NotificationRequest notificationRequest = new NotificationRequest(title, body);
      databaseReference.push().setValue(notificationRequest, (error, ref) -> {
        if (error != null) {
          System.err.println("Error saving data: " + error.getMessage());
        } else {
          System.out.println("Successfully saved data");
        }
      });

      return ResponseEntity.ok("Successfully sent message: " + response);
    } catch (FirebaseMessagingException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to send notification: " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Unexpected error: " + e.getMessage());
    }
  }
}
