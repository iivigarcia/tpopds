package com.uade.tpo.model.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailAdapterImpl implements MailAdapter {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void enviarMail(String to, String subject, String text) {
        // Solo enviar al mail propio para evitar bloqueos de gmail
        if (!"ivangarcia354@gmail.com".equalsIgnoreCase(to)) {
            System.out.println("Destinatario ignorado para no generar SPAM");
            return;
        }
        System.out.println("Enviando correo electronico al destinatario");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
        }
    }
} 