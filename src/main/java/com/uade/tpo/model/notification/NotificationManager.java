package com.uade.tpo.model.notification;

import com.uade.tpo.model.Partido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationManager implements NotificationSubject {
    
    private final List<NotificationObserver> observers = new ArrayList<>();
    @Autowired
    private EmailNotificationStrategy emailNotificationStrategy;
    private PushNotificationStrategy pushNotificationStrategy;
    private NotificationStrategy currentNotificationStrategy;

    public NotificationManager(EmailNotificationStrategy emailStrategy,PushNotificationStrategy pushStrategy) {
        this.emailNotificationStrategy = emailStrategy;
        this.pushNotificationStrategy = pushStrategy;
        this.currentNotificationStrategy = emailNotificationStrategy; // opcionalmente setear una por defecto
    }
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
            currentNotificationStrategy.sendNotification(partido,eventType);
        }
    }
    
    private NotificationStrategy shouldNotifyObserver(NotificationObserver observer) {
        return currentNotificationStrategy;
    }
    
    public void setNotificationType(String strategyType) {
        switch (strategyType.toUpperCase()) {
            case "EMAIL":
                this.currentNotificationStrategy = emailNotificationStrategy;
                break;
            case "PUSH":
                this.currentNotificationStrategy = pushNotificationStrategy;
                break;
            default:
                throw new IllegalArgumentException("Tipo de estrategia no válida: " + strategyType);
        }
        System.out.println("Tipo de notificación cambiado a: " + strategyType);
    }
    
    public String getCurrentNotificationType() {
        return currentNotificationStrategy.getStrategyName();
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