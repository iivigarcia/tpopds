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
        if (!"santiagonahuellopez@gmail.com".equalsIgnoreCase(to)) {
            System.out.println("Destinatario ignorado para no generar SPAM: " + to);
            return;
        }

        System.out.println("Enviando correo electronico a: " + to);
        System.out.println("Asunto: " + subject);
        System.out.println("Mensaje: " + text);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("ivangarcia354@gmail.com"); // Establecer remitente explícitamente

            emailSender.send(message);
            System.out.println("Email enviado exitosamente a: " + to);
        } catch (Exception e) {
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getSimpleName());
            e.printStackTrace();

            // Si es un error de certificado, sugerir alternativas
            if (e.getMessage().contains("PKIX") || e.getMessage().contains("certification path")) {
                System.err.println("Sugerencia: Verifica la configuración SSL/TLS en application.properties");
                System.err.println("O prueba usando la configuración alternativa con puerto 465");
            }
        }
    }
}