package com.example.demo.service;

import com.example.demo.model.NotificationRequest;
import com.example.demo.model.User;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserService userService;

    private final DatabaseReference databaseReference;

    public PushNotificationService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void sendNotification(NotificationRequest notificationRequest, User user) {
        try {
            sendToQueue(user.getUserId(), notificationRequest);
            saveNotificationToFirebase(user.getUserId(), notificationRequest);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }

    public void sendNotification(NotificationRequest notificationRequest) {
        String userId = notificationRequest.getUserId();
        if (userId != null) {
            userService.getUserById(userId, new UserFetchCallback() {
                @Override
                public void onUserFetched(User user) {
                    if (user == null) {
                        System.err.println("User not found for ID: " + userId);
                    } else {
                        sendNotification(notificationRequest, user);
                    }
                }
            });
        } else {
            System.err.println("User ID is null in the notification request.");
        }
    }

    private void sendToQueue(String userId, NotificationRequest notificationRequest) {
        rabbitTemplate.convertAndSend("notificationExchange", "notificationQueue", notificationRequest);
        // Implement additional logic to send notification using userId if needed
        System.out.println("Notification sent to queue for user: " + userId);
    }

    private void saveNotificationToFirebase(String userId, NotificationRequest notificationRequest) {
        String notificationId = databaseReference.child("notifications").child(userId).push().getKey();
        if (notificationId != null) {
            ApiFuture<Void> future = databaseReference.child("notifications").child(userId).child(notificationId).setValueAsync(notificationRequest);

            // Adding a listener to handle success and failure cases
            ApiFutures.addCallback(future, new ApiFutureCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Notification saved in Firebase RTDB under user: " + userId + " with ID: " + notificationId);
                }

                @Override
                public void onFailure(Throwable t) {
                    System.err.println("Failed to save notification: " + t.getMessage());
                }
            }, MoreExecutors.directExecutor());
        } else {
            System.err.println("Failed to save notification in Firebase RTDB: Unable to generate ID");
        }
    }
}
