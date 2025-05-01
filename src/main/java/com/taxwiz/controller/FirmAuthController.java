package com.taxwiz.controller;

import com.taxwiz.model.FirmResponseEntity;
import com.taxwiz.service.firm.FirmAuthService;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;

@RestController
@RequestMapping("/api/firm/auth")
public class FirmAuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final FirmAuthService firmAuthService;

    FirmAuthController(FirmAuthService firmAuthService) {
        this.firmAuthService = firmAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<FirmResponseEntity> registerFirm(@RequestBody RequestEntity registerRequest) {
        firmAuthService.registerFirm();
        return ResponseEntity.ok(new FirmResponseEntity());
    }
}
