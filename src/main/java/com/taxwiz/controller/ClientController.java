package com.taxwiz.controller;

import com.taxwiz.dto.ClientDto;
import com.taxwiz.dto.ErrorResponseDto;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.service.client.ClientService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<?> createClient(@RequestHeader ("Authorization") String authorization, @RequestBody ClientDto clientDto) {
        log.info("Creating client");
        try {
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

}
