package com.taxwiz.service.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.taxwiz.utils.ErrorMessages.MAIL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setFrom(System.getenv("GMAIL_USERNAME"));
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Error Sending mail due to: {}", e.getMessage());
            throw new RuntimeException(MAIL_ERROR.name());
        }

    }
}
