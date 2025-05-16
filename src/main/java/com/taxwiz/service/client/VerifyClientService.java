package com.taxwiz.service.client;

import com.taxwiz.exception.NotFoundException;
import com.taxwiz.model.Client;
import com.taxwiz.repository.ClientRepository;
import com.taxwiz.service.auth.JwtSetup;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.taxwiz.utils.ErrorMessages.INVALID_TOKEN;
import static com.taxwiz.utils.ErrorMessages.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyClientService {

    private final ClientRepository clientRepository;
    private final JwtSetup jwtSetup;

    public void verifyClient(String token) {
        try {
            String tokenType = jwtSetup.extractClaim(token, claims -> claims.get("type", String.class));
            String clientUid = jwtSetup.extractClaim(token, claims -> claims.get("clientUid", String.class));
            if (tokenType == null || !tokenType.equals("client-onboarding") || clientUid == null) {
                log.error("Invalid token type");
                throw new RuntimeException(INVALID_TOKEN.name());
            }
            Client client = clientRepository.findByUid(clientUid);
            if (client == null) {
                log.error("Client not found");
                throw new NotFoundException(NOT_FOUND.name());
            }
            client.setVerified(true);
            clientRepository.save(client);
            log.info("Client {} verified successfully", client.getName());
        } catch (RuntimeException e) {
            log.error("Error verifying client: {}", e.getMessage());
            throw new RuntimeException("Error verifying client");
        }
    }
}
