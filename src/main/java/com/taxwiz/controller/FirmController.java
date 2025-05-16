package com.taxwiz.controller;

import com.taxwiz.dto.ErrorResponseDto;
import com.taxwiz.dto.firm.FirmDto;
import com.taxwiz.dto.firm.FirmResponseDto;
import com.taxwiz.exception.AlreadyExistsException;
import com.taxwiz.model.Firm;
import com.taxwiz.service.firm.FirmRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/firm")
@RequiredArgsConstructor
public class FirmController {

    private final FirmRegistrationService firmRegistrationService;

    @PreAuthorize("hasRole('ROOT')")
    @PostMapping("/register")
    public ResponseEntity<?> registerFirm(@RequestBody FirmDto firmDto) {
        log.info("Registering firm: {}", firmDto.getName());
        Firm firm = null;
        try {
            firm = firmRegistrationService.registerFirm(firmDto);
        } catch (AlreadyExistsException e) {
            log.error("Firm {} already exists", firmDto.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(e.getMessage()));
        } catch (Exception e) {
            log.error("Error while registering firm: {}", firmDto.getName());
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new
                        FirmResponseDto(
                            firm.getUid(),
                            firm.getName(),
                            firm.getGstNo(),
                            firm.getAddress(),
                            firm.getEmail(),
                            firm.getPhone()
                        )
                );

    }

}
