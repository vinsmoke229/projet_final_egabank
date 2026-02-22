package com.ega.ebank_backend.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Async
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendNotificationEmail(String to, String subject, String body) {
        //affichage dans la console
        System.out.println("------------------------------------------");
        System.out.println("SIMULATION EMAIL POUR : " + to);
        System.out.println("SUJET : " + subject);
        System.out.println("CONTENU : " + body);
        System.out.println("------------------------------------------");
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("noreply@ega-bank.com");

            helper.setTo(to);

            helper.setSubject(subject);

            helper.setText(body, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("Erreur envoi email: " + e.getMessage());
//
        }
    }
}