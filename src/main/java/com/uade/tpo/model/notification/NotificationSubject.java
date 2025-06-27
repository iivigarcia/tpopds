package com.uade.tpo.model.notification;

public interface NotificationSubject {
    void registerObserver(NotificationObserver observer);
    void removeObserver(NotificationObserver observer);
    void notifyObservers(com.uade.tpo.model.Partido partido, String eventType);
} 