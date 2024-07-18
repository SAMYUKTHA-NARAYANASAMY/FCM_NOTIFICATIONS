package com.example.demo.controller;

import com.example.demo.model.NotificationRequest;
import com.example.demo.model.User;
import com.example.demo.service.PushNotificationService;
import com.example.demo.service.UserFetchCallback;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private UserService userService;

    @PostMapping("/send-individuals")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest notificationRequest,User user) {
        String userId = notificationRequest.getUserId();
        if (userId != null) {
            userService.getUserById(userId, new UserFetchCallback() {
                @Override
                public void onUserFetched(User user) {
                    if (user != null) {
                        pushNotificationService.sendNotification(notificationRequest, user);
                    }
                }
            });
            return ResponseEntity.ok("Notification sent successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID is missing in the notification request.");
        }
    }

    @PostMapping("/send-groups")
    public ResponseEntity<String> sendNotificationToGroup(@RequestBody NotificationRequest notificationRequest) {
        if (notificationRequest.getUserIds() != null && !notificationRequest.getUserIds().isEmpty()) {
            for (String userId : notificationRequest.getUserIds()) {
                userService.getUserById(userId, new UserFetchCallback() {
                    @Override
                    public void onUserFetched(User user) {
                        if (user != null) {
                            NotificationRequest individualNotification = new NotificationRequest();
                            individualNotification.setTitle(notificationRequest.getTitle());
                            individualNotification.setBody(notificationRequest.getBody());
                            individualNotification.setUserId(userId);
                            pushNotificationService.sendNotification(individualNotification, user);
                        }
                    }
                });
            }
            return ResponseEntity.ok("Notifications sent to group successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User IDs are missing in the notification request.");
        }
    }
}
