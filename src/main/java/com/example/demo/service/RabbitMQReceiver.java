package com.example.demo.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.example.demo.model.NotificationRequest;

@Service
public class RabbitMQReceiver {

    private final PushNotificationService pushNotificationService;

    public RabbitMQReceiver(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @RabbitListener(queues = "notificationQueue")
    public void receiveNotification(NotificationRequest notificationRequest) {
        try {
            if (notificationRequest.getUserId() != null) {
                pushNotificationService.sendNotification(notificationRequest);
            } else if (notificationRequest.getUserIds() != null && !notificationRequest.getUserIds().isEmpty()) {
                for (String userId : notificationRequest.getUserIds()) {
                    NotificationRequest individualNotification = new NotificationRequest();
                    individualNotification.setTitle(notificationRequest.getTitle());
                    individualNotification.setBody(notificationRequest.getBody());
                    individualNotification.setUserId(userId);
                    pushNotificationService.sendNotification(individualNotification);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
