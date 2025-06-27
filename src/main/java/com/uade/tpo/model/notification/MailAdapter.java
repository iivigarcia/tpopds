package com.uade.tpo.model.notification;

public interface MailAdapter {
    void enviarMail(String to, String subject, String text);
} 