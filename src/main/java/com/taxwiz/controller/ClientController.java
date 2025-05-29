package com.taxwiz.controller;

import com.taxwiz.dto.ErrorResponseDto;
import com.taxwiz.dto.client.AssignClientDto;
import com.taxwiz.dto.client.ClientDto;
import com.taxwiz.exception.BadCredentialsException;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.exception.UnverifiedException;
import com.taxwiz.service.client.AssignClientService;
import com.taxwiz.service.client.ClientService;
import com.taxwiz.service.client.VerifyClientService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.taxwiz.utils.ErrorMessages.INVALID_TOKEN;

@Slf4j
@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final VerifyClientService verifyClientService;
    private final AssignClientService assignClientService;

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

    @GetMapping("/fetch-by-user")
    @PreAuthorize("hasAuthority('FIRM_USER')")
    public ResponseEntity<?> fetchClientByUser(@RequestHeader ("Authorization") String authorization) {
        log.info("Fetching client by user");
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(grantedAuthority -> log.info("Authority: {}", grantedAuthority.getAuthority()));
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

    @GetMapping("/unverified")
    @CrossOrigin("*")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> fetchUnverifiedClients(@RequestHeader ("Authorization") String authorization) {
        log.info("Fetching unverified clients");
        try {
            String token = authorization.substring(7);
            return ResponseEntity.ok(clientService.fetchAllUnverifiedClients(token));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("/verified")
    @CrossOrigin("*")
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

    @CrossOrigin("*")
    @GetMapping("/unassigned")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> fetchUnassignedClients(@RequestHeader ("Authorization") String authorization) {
        log.info("Fetching unassigned clients");
        try {
            String token = authorization.substring(7);
            return ResponseEntity.ok(clientService.fetchUnassignedClients(token));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> assignClientToEmployee(@RequestHeader ("Authorization") String authorization, @RequestBody AssignClientDto assignClientDto) {
        log.info("Assigning client to employee");
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(INVALID_TOKEN.name()));
            }
            String token = authorization.substring(7);
            assignClientService.assignClientToEmployee(assignClientDto.getClientUid(), assignClientDto.getEmployeeUid(), token);
            return ResponseEntity.ok("Client assigned to employee successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException | UnverifiedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/name/{clientUid}")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> updateClient(@RequestHeader ("Authorization") String authorization, @PathVariable String clientUid, @RequestBody ClientDto clientDto) {
        log.info("Updating client with UID: {}", clientUid);
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(INVALID_TOKEN.name()));
            }
            String token = authorization.substring(7);
            clientService.updateClient(clientUid, clientDto, token);
            return ResponseEntity.ok("Client updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/email/{clientUid}")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> updateClientEmail(@RequestHeader ("Authorization") String authorization, @PathVariable String clientUid, @RequestBody ClientDto clientDto) {
        log.info("Updating client email with UID: {}", clientUid);
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(INVALID_TOKEN.name()));
            }
            String token = authorization.substring(7);
            clientService.updateClientEmail(clientUid, clientDto, token);
            return ResponseEntity.ok("Client email updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/phone/{clientUid}")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> updateClientPhone(@RequestHeader ("Authorization") String authorization, @PathVariable String clientUid, @RequestBody ClientDto clientDto) {
        log.info("Updating client phone with UID: {}", clientUid);
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(INVALID_TOKEN.name()));
            }
            String token = authorization.substring(7);
            clientService.updateClientPhone(clientUid, clientDto, token);
            return ResponseEntity.ok("Client phone updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/address/{clientUid}")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> updateClientAddress(@RequestHeader ("Authorization") String authorization, @PathVariable String clientUid, @RequestBody ClientDto clientDto) {
        log.info("Updating client address with UID: {}", clientUid);
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(INVALID_TOKEN.name()));
            }
            String token = authorization.substring(7);
            clientService.updateClientAddress(clientUid, clientDto, token);
            return ResponseEntity.ok("Client address updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/gst/{clientUid}")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> updateClientGst(@RequestHeader ("Authorization") String authorization, @PathVariable String clientUid, @RequestBody ClientDto clientDto) {
        log.info("Updating client GST with UID: {}", clientUid);
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(INVALID_TOKEN.name()));
            }
            String token = authorization.substring(7);
            clientService.updateClientGst(clientUid, clientDto, token);
            return ResponseEntity.ok("Client GST updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (ExpiredJwtException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

}
