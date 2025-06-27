package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationStrategy implements NotificationStrategy {
    
    @Override
    public void sendNotification(Partido partido, String eventType) {
        System.out.println("Email notification sent for event: " + eventType + " - Partido: " + partido.getId());
    }
    
    @Override
    public String getStrategyName() {
        return "EMAIL";
    }
} 