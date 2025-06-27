package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;

public interface NotificationObserver {
    void update(Partido partido, String eventType);
} 