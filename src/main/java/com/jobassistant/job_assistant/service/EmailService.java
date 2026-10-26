package com.jobassistant.job_assistant.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String recipientEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(recipientEmail);
        message.setSubject("AI Job Assistant - Email Verification");
        message.setText(
                "Hello!\n\n"
                + "Your email verification code is: " + code + "\n\n"
                + "Enter this code in AI Job Assistant to verify your email.\n"
                + "If you did not request this, you can ignore this email."
        );

        mailSender.send(message);
    }
}