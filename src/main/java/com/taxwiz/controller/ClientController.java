package com.taxwiz.controller;

import com.taxwiz.dto.ClientDto;
import com.taxwiz.dto.ErrorResponseDto;
import com.taxwiz.exception.BadCredentialsException;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.service.client.ClientService;
import com.taxwiz.service.client.VerifyClientService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.taxwiz.utils.ErrorMessages.INVALID_TOKEN;

@Slf4j
@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final VerifyClientService verifyClientService;

    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createClient(@RequestHeader ("Authorization") String authorization, @RequestBody ClientDto clientDto) {
        log.info("Creating client");
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(INVALID_TOKEN.name()));
            }
            String token = authorization.substring(7);
            clientService.createClient(clientDto, token);
            return ResponseEntity.ok("Client created successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PostMapping("/onboarding")
    public ResponseEntity<?> verifyClient(@RequestParam String token) {
        log.info("Onboarding client");
        try {
            log.info("Verifying client with token: {}", token);
            verifyClientService.verifyClient(token);
            return ResponseEntity.ok("Client onboarded successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("fetchAll")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> fetchAllClients(@RequestHeader ("Authorization") String authorization) {
        log.info("Fetching all clients");
        try {
            String token = authorization.substring(7);
            return ResponseEntity.ok(clientService.fetchAllClients(token));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }
}
