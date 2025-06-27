package com.uade.tpo.config;

import com.uade.tpo.model.notification.NotificationManager;
import com.uade.tpo.model.notification.EmailNotificationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class NotificationConfig {
    
    @Autowired
    private NotificationManager notificationManager;
    
    @Autowired
    private EmailNotificationStrategy emailNotificationStrategy;
    
    @PostConstruct
    public void initializeNotificationSystem() {
        // Set default strategy to EMAIL
        notificationManager.setStrategy(emailNotificationStrategy);
        
        System.out.println("Notification system initialized with default strategy: EMAIL");
    }
} 