package com.taxwiz.service.auth;

import com.taxwiz.dto.LoginDto;
import com.taxwiz.exception.BadCredentialsException;
import com.taxwiz.exception.UserNotFoundException;
import com.taxwiz.model.Role;
import com.taxwiz.model.User;
import com.taxwiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final JwtSetup jwtSetup;

    public String userLogin(LoginDto loginDto) throws UserNotFoundException, BadCredentialsException {
        log.info("Initiating Login");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(loginDto.getPassword());
        User user = userRepository.findByUsername(loginDto.getUsername());
        String token = null;
        if (user != null) {
            log.info("User {} found", user.getUsername());
            if (encoder.matches(loginDto.getPassword(), user.getPassword())) {
                log.info("User Verified");
                Map<String, Object> claims = Map.of(
                        "roles",
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                        );

                token = jwtSetup.generateToken(claims, user.getUsername());
                log.info("Token generated");
            } else {
                log.error("Invalid password for user: {}", loginDto.getUsername());
                throw new BadCredentialsException("Invalid password");
            }
        } else {
            log.error("User {} not found", loginDto.getUsername());
            throw new UserNotFoundException("Invalid password");
        }

        return token;
    }
}
