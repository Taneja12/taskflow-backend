package com.deepanshu.backend.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTestEmail(String recipient) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipient);
        message.setSubject("TaskFlow - SMTP Test");
        message.setText(
                "Congratulations! Your TaskFlow backend "
                        + "is successfully connected to Brevo SMTP."
        );

        mailSender.send(message);
    }

    public void sendWorkspaceCreatedEmail(
            String recipient,
            String ownerName,
            String workspaceName
    ){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipient);
        message.setSubject("Welcome to Taskflow!");

        message.setText(
                "Hello " + ownerName + ",\n\n"
                        + "Your workspace '" + workspaceName
                        + "' has been successfully created.\n\n"
                        + "You can now start organizing your projects "
                        + "and tasks in TaskFlow.\n\n"
                        + "Regards,\nTaskFlow Team"
        );

        mailSender.send(message);
    }
}
