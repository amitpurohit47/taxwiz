package com.taxwiz.service.auth;

import com.taxwiz.exception.BadCredentialsException;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.model.Role;
import com.taxwiz.model.User;
import com.taxwiz.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import static com.taxwiz.utils.ErrorMessages.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetPasswordService {

    @Value("${jwt.expiration.login}")
    private Long expirationTime;

    private final JwtSetup jwtSetup;
    private final UserRepository userRepository;

    public String setPassword(String password, String token) throws BadCredentialsException, RuntimeException {
        String loginToken = null;
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String username = jwtSetup.extractClaim(token, Claims::getSubject);
            Claims claims = jwtSetup.extractClaims(token);
            Long firmId = claims.get("firmId", Long.class);
            String type = claims.get("type", String.class);
            log.info("Token type: {}", type);
            if (!type.equals("password-reset")) {
                log.info("Invalid token type: {}", type);
                throw new BadCredentialsException(INVALID_TOKEN.name());
            }
            User user = userRepository.findByUsername(username);
            if ( user == null) {
                log.info("User {} not found", username);
                throw new NotFoundException();
            }
            if (!user.getFirm().getId().equals(firmId)) {
                log.info("User {} does not belong to firm {}", username, firmId);
                throw new BadCredentialsException();
            }
            String encodedPassword = encoder.encode(password);
            user.setPassword(encodedPassword);
            user.setVerified(true);
            userRepository.save(user);
            Map<String, Object> loginClaims = Map.of(
                    "roles",
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),
                    "type",
                    "login"
            );
            loginToken = jwtSetup.generateToken(loginClaims, username, Duration.ofMillis(expirationTime));
        } catch (IllegalArgumentException e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new BadCredentialsException();
        } catch (ExpiredJwtException e) {
            log.error("Token expired");
            throw new RuntimeException(TOKEN_EXPIRED.name());
        }

        return loginToken;
    }
}
