package com.taxwiz.service.utils;

import com.taxwiz.exception.NotFoundException;
import com.taxwiz.model.User;
import com.taxwiz.repository.UserRepository;
import com.taxwiz.service.auth.JwtSetup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final JwtSetup jwtSetup;

    @Value("${jwt.expiration.password-reset}")
    private Long expirationTime;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendPasswordResetEmail(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        Map<String, Object> claims = Map.of(
                "firmId",
                user.getFirm().getId(),
                "type",
                "password-reset"
        );
        String token = jwtSetup.generateToken(claims, username, Duration.ofMillis(expirationTime));
        String resetLink = String.format("%s/api/user/auth/set-password?token=%s", baseUrl, token);
        String subject = "Password Reset Request";
        String body = String.format("Click the link to set your password: %s", resetLink);
        emailService.sendEmail(username, subject, body);
    }
}
