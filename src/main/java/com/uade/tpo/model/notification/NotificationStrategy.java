package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;

public interface NotificationStrategy {
    
    void sendNotification(Partido partido, String eventType);

    String getStrategyName();
} 