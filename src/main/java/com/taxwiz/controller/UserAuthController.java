package com.taxwiz.controller;

import com.taxwiz.dto.ErrorResponseDto;
import com.taxwiz.dto.LoginDto;
import com.taxwiz.dto.ResetPasswordDto;
import com.taxwiz.exception.BadCredentialsException;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.service.auth.JwtSetup;
import com.taxwiz.service.auth.LoginService;
import com.taxwiz.service.auth.SetPasswordService;
import io.jsonwebtoken.Claims;
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
    private final SetPasswordService setPasswordService;
    private final JwtSetup jwtSetup;

    // TODO: Check why already logged in is not working

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginDto loginDto, @RequestHeader(value = "Authorization", required = false) String authorizationHeader ) {
        String token = null;
        try {
            if ( authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ) {
                token = authorizationHeader.substring(7);
                if ( jwtSetup.isTokenValid(token, loginDto.getUsername()) ) {
                    Claims claims = jwtSetup.extractClaims(token);
                    String type = claims.get("type", String.class);
                    if (type != null && type.equals("login")) {
                        log.info("User already logged in");
                        return ResponseEntity.ok(token);
                    }
                }
            }
            token = loginService.userLogin(loginDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody ResetPasswordDto passwordDto) {
        log.info("Setting password");
        try {
            String tokenResponse = setPasswordService.setPassword(passwordDto.getPassword(), token);
            return ResponseEntity.ok(tokenResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

}
