package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;

public interface NotificationSubject {
    void registerObserver(NotificationObserver observer);
    void removeObserver(NotificationObserver observer);
    void notifyObservers(Partido partido, String eventType);
} 