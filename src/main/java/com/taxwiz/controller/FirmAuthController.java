package com.taxwiz.controller;

import com.taxwiz.dto.FirmDto;
import com.taxwiz.dto.PasswordChange;
import com.taxwiz.model.FirmResponseEntity;
import com.taxwiz.service.firm.FirmAuthService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

@Slf4j
@RestController
@RequestMapping("/api/firm/auth")
public class FirmAuthController {

    private final FirmAuthService firmAuthService;

    FirmAuthController(FirmAuthService firmAuthService) {
        this.firmAuthService = firmAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<FirmResponseEntity> registerFirm(@RequestBody FirmDto registerRequest) {
        String passwordLink = firmAuthService.registerFirm(registerRequest);
        return ResponseEntity.ok(new FirmResponseEntity(registerRequest.getFirmName(), passwordLink));
    }

    @PostMapping("/set-password")
    public ResponseEntity setPassword(@RequestParam String token, @RequestBody PasswordChange passwordChange) {
        log.info("Setting password for firm with JWT: {}", passwordChange.getJwt());
        firmAuthService.setPassword(token, passwordChange);
        return ResponseEntity.ok().build();
    }
}
