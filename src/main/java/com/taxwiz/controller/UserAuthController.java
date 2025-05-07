package com.taxwiz.controller;

import com.taxwiz.dto.LoginDto;
import com.taxwiz.exception.BadCredentialsException;
import com.taxwiz.exception.UserNotFoundException;
import com.taxwiz.service.auth.JwtSetup;
import com.taxwiz.service.auth.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final LoginService loginService;
    private final JwtSetup jwtSetup;

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginDto loginDto, @RequestHeader(value = "Authorization", required = false) String authorizationHeader ) {
        String token = null;
        try {
            if ( authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ) {
                token = authorizationHeader.substring(7);
                if ( jwtSetup.isTokenValid(token, loginDto.getUsername()) ) {
                    log.info("User already logged in");
                    return ResponseEntity.ok(token);
                }
            }
            token = loginService.userLogin(loginDto);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
        return ResponseEntity.ok(token);
    }

}
