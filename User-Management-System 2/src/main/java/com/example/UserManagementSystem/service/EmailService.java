package com.example.UserManagementSystem.service;

import com.example.UserManagementSystem.Model.Users;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String token) throws MessagingException {
        log.info("Incoming Email is : "+ email);
       String verificationLink = "http://localhost:8080/verify?token="+token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(email);
        helper.setSubject("Email Verification");
        helper.setText("Click the link to verify your email: " + verificationLink, true);

        mailSender.send(message);
  log.info("Mail is sent "+message);
    }
}
