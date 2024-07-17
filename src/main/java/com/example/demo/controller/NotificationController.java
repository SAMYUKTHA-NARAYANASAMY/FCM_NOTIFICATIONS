package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.NotificationRequest;
import com.example.demo.service.PushNotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController { 

  @Autowired
  private PushNotificationService pushNotificationService;

  @PostMapping("/send")
  public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest notification) {
    try {
      return pushNotificationService.sendNotificationToAll(notification.getTitle(), notification.getBody());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Unexpected error occurred: " + e.getMessage());
    }
  }
   
  @GetMapping("/test")
  public ResponseEntity<String> test() {
    return ResponseEntity.ok("testing....");
  }
}
