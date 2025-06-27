package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationManager implements NotificationSubject {
    
    private final List<NotificationObserver> observers = new ArrayList<>();
    private NotificationType currentNotificationType = NotificationType.EMAIL;
    
    @Override
    public void registerObserver(NotificationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(Partido partido, String eventType) {
        for (NotificationObserver observer : observers) {
            if (shouldNotifyObserver(observer)) {
                observer.update(partido, eventType);
            }
        }
    }
    
    private boolean shouldNotifyObserver(NotificationObserver observer) {
        switch (currentNotificationType) {
            case EMAIL:
                return observer instanceof EmailNotificationService;
            case PUSH:
                return observer instanceof PushNotificationService;
            default:
                return false;
        }
    }
    
    public void setNotificationType(NotificationType notificationType) {
        this.currentNotificationType = notificationType;
        System.out.println("Tipo de notificación cambiado a: " + notificationType);
    }
    
    public NotificationType getCurrentNotificationType() {
        return currentNotificationType;
    }
    
    public void notifyPartidoCreated(Partido partido) {
        notifyObservers(partido, "PARTIDO_CREADO");
    }
    
    public void notifyPartidoArmado(Partido partido) {
        notifyObservers(partido, "PARTIDO_ARMADO");
    }
    
    public void notifyPartidoConfirmado(Partido partido) {
        notifyObservers(partido, "PARTIDO_CONFIRMADO");
    }
    
    public void notifyPartidoEnJuego(Partido partido) {
        notifyObservers(partido, "PARTIDO_EN_JUEGO");
    }
    
    public void notifyPartidoFinalizado(Partido partido) {
        notifyObservers(partido, "PARTIDO_FINALIZADO");
    }
    
    public void notifyPartidoCancelado(Partido partido) {
        notifyObservers(partido, "PARTIDO_CANCELADO");
    }
} 