package com.uade.tpo.config;

import com.uade.tpo.model.notification.NotificationManager;
import com.uade.tpo.model.notification.EmailNotificationService;
import com.uade.tpo.model.notification.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import jakarta.annotation.PostConstruct;

@Configuration
@DependsOn({"emailNotificationService", "pushNotificationService"})
public class NotificationConfig {
    
    @Autowired
    private NotificationManager notificationManager;
    
    @Autowired
    private EmailNotificationService emailNotificationService;
    
    @Autowired
    private PushNotificationService pushNotificationService;
    
    @PostConstruct
    public void initializeNotificationSystem() {
        // Register observers
        notificationManager.registerObserver(emailNotificationService);
        notificationManager.registerObserver(pushNotificationService);
        
        System.out.println("Notification system initialized with observers: Email and Push");
    }
} 