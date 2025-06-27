package com.uade.tpo.model.notification;

import java.util.ArrayList;
import java.util.List;

public abstract class Observado {
    private List<IObservador> observadores = new ArrayList<>();

    public void add(IObservador observer) {
        observadores.add(observer);
    };

    public void remove(IObservador observer) {
        observadores.remove(observer);
    };

    public void notifyObservers() {
        System.out.println("Observado.notifyObservers() - Cantidad de observadores: " + observadores.size());
        for (IObservador observer : observadores) {
            System.out.println("Notificando a observador: " + observer.getClass().getSimpleName());
            observer.serNotificado();
        }
    };

}