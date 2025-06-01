package com.taxwiz.service.client;


import com.taxwiz.dto.client.ClientDto;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.model.Client;
import com.taxwiz.model.User;
import com.taxwiz.repository.ClientRepository;
import com.taxwiz.repository.UserRepository;
import com.taxwiz.service.auth.JwtSetup;
import com.taxwiz.service.utils.EmailService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.taxwiz.utils.ErrorMessages.*;
import static com.taxwiz.utils.RoleInfo.FIRM_ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    @Value("${jwt.expiration.client-onboarding}")
    private Long expirationTime;

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtSetup jwtSetup;

    public Client createClient(ClientDto clientDto, String token) {
        try {
            String username = jwtSetup.extractClaim(token, Claims::getSubject);
            User user = userRepository.findByUsername(username);
            if ( user == null ) {
                log.info("User {} not found", username);
                throw new RuntimeException(NOT_FOUND.name());
            }
            Client client = new Client();
            client.setName(clientDto.getName());
            client.setEmail(clientDto.getEmail());
            client.setPhone(clientDto.getPhone());
            client.setAddress(clientDto.getAddress());
            client.setGstNo(client.getGstNo());
            client.setFirm(user.getFirm());
            client.setVerified(false);

            Client savedClient = clientRepository.save(client);
            log.info("Client {} created successfully", client.getName());

            Map<String, Object> claims = Map.of(
                    "clientUid", savedClient.getUid(),
                    "type", "client-onboarding"
            );
            String onboardingToken = jwtSetup.generateToken(claims, client.getEmail(), Duration.ofMillis(expirationTime));
            String onboardingLink = String.format("http://localhost:8080/api/client/onboarding?token=%s", onboardingToken);
            String subject = "Client Onboarding";
            String body = String.format("Click the link to complete your onboarding: %s", onboardingLink);
            emailService.sendEmail(client.getEmail(), subject, body);
            log.info("Onboarding email sent to {}", client.getEmail());
            return savedClient;

        } catch (MailException e) {
            log.info("Error sending email: {}", e.getMessage());
            throw new RuntimeException(MAIL_ERROR.name());
        } catch (Exception e) {
            log.error("Error creating client: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ClientDto> fetchAllClients(String token) {
            String username = jwtSetup.extractClaim(token, Claims::getSubject);
            User user = userRepository.findByUsername(username);
            if ( user == null ) {
                log.info("User {} not found", username);
                throw new NotFoundException(NOT_FOUND.name());
            }
            // Return a boolean if the user is a firm admin

            boolean isFirmAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals(FIRM_ADMIN.name()));
            List<Client> clients = isFirmAdmin ? clientRepository.findAllByFirmId(user.getFirm().getId()) : clientRepository.findAllByEmployeeId(user.getId());

            return clients.stream()
                    .filter(Client::isVerified)
                    .map(client -> new ClientDto(client.getUid(), client.getName(), client.getEmail(), client.getGstNo(), client.getPhone(), client.getAddress()))
                    .toList();
    }

    public List<ClientDto> fetchAllUnverifiedClients(String token) {
        String username = jwtSetup.extractClaim(token, Claims::getSubject);
        User user = userRepository.findByUsername(username);
        if ( user == null ) {
            log.info("User {} not found", username);
            throw new NotFoundException(NOT_FOUND.name());
        }
        List<Client> clients = clientRepository.findAllByFirmId(user.getFirm().getId());
        return clients.stream()
                .filter(client -> !client.isVerified())
                .map(client -> new ClientDto(client.getUid(), client.getName(), client.getEmail(), client.getGstNo(), client.getPhone(), client.getAddress()))
                .toList();
    }

    public List<ClientDto> fetchUnassignedClients(String token) {
        String username = jwtSetup.extractClaim(token, Claims::getSubject);
        User user = userRepository.findByUsername(username);
        if ( user == null ) {
            log.info("User {} not found", username);
            throw new NotFoundException(NOT_FOUND.name());
        }
        List<Client> clients = clientRepository.findAllByFirmId(user.getFirm().getId());
        return clients.stream()
                .filter(client -> client.getEmployee() == null)
                .map(client -> new ClientDto(client.getUid(), client.getName(), client.getEmail(), client.getGstNo(), client.getPhone(), client.getAddress()))
                .toList();
    }

    public Client updateClientGst(String uid, ClientDto clientDto, String token) {
        String username = jwtSetup.extractClaim(token, Claims::getSubject);
        User user = userRepository.findByUsername(username);
        if ( user == null ) {
            log.info("User {} not found", username);
            throw new NotFoundException(NOT_FOUND.name());
        }
        Client client = clientRepository.findByUid(uid);
        if ( client == null ) {
            log.info("Client with UID {} not found", uid);
            throw new NotFoundException(NOT_FOUND.name());
        }
        client.setGstNo(clientDto.getGstNo());
        return clientRepository.save(client);
    }

    public Client updateClientEmail(String uid, ClientDto clientDto, String token) {
        String username = jwtSetup.extractClaim(token, Claims::getSubject);
        User user = userRepository.findByUsername(username);
        if ( user == null ) {
            log.info("User {} not found", username);
            throw new NotFoundException(NOT_FOUND.name());
        }
        Client client = clientRepository.findByUid(uid);
        if ( client == null ) {
            log.info("Client with UID {} not found", uid);
            throw new NotFoundException(NOT_FOUND.name());
        }
        client.setEmail(clientDto.getEmail());
        return clientRepository.save(client);
    }

    public Client updateClientPhone(String uid, ClientDto clientDto, String token) {
        String username = jwtSetup.extractClaim(token, Claims::getSubject);
        User user = userRepository.findByUsername(username);
        if ( user == null ) {
            log.info("User {} not found", username);
            throw new NotFoundException(NOT_FOUND.name());
        }
        Client client = clientRepository.findByUid(uid);
        if ( client == null ) {
            log.info("Client with UID {} not found", uid);
            throw new NotFoundException(NOT_FOUND.name());
        }
        client.setPhone(clientDto.getPhone());
        return clientRepository.save(client);
    }

    public Client updateClientAddress(String uid, ClientDto clientDto, String token) {
        String username = jwtSetup.extractClaim(token, Claims::getSubject);
        User user = userRepository.findByUsername(username);
        if ( user == null ) {
            log.info("User {} not found", username);
            throw new NotFoundException(NOT_FOUND.name());
        }
        Client client = clientRepository.findByUid(uid);
        if ( client == null ) {
            log.info("Client with UID {} not found", uid);
            throw new NotFoundException(NOT_FOUND.name());
        }
        client.setAddress(clientDto.getAddress());
        return clientRepository.save(client);
    }

    public Client updateClient(String uid, ClientDto clientDto, String token) {
        String username = jwtSetup.extractClaim(token, Claims::getSubject);
        User user = userRepository.findByUsername(username);
        if ( user == null ) {
            log.info("User {} not found", username);
            throw new NotFoundException(NOT_FOUND.name());
        }
        Client client = clientRepository.findByUid(uid);
        if ( client == null ) {
            log.info("Client with UID {} not found", uid);
            throw new NotFoundException(NOT_FOUND.name());
        }
        client.setName(clientDto.getName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        return clientRepository.save(client);
    }

}
