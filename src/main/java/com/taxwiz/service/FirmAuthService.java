package com.taxwiz.service;

import com.taxwiz.auth.JwtSetup;
import com.taxwiz.dto.FirmDto;
import com.taxwiz.dto.PasswordChange;
import com.taxwiz.model.Firm;
import com.taxwiz.repository.firm.FirmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirmAuthService {

    private final FirmRepository firmRepository;
    private final EmailService emailService;
    private final JwtSetup jwtSetup;

    public String registerFirm(FirmDto firmRegisterRequest) {
        saveFirm(firmRegisterRequest);
        String token = jwtSetup.generateToken(firmRegisterRequest.getFirmName(), "password-change");
        String passwordChangeLink = "http://localhost:8080/api/firm/auth/set-password?token=" + token;
        emailService.sendMail(firmRegisterRequest.getEmail(), "Password update", "Kindly update your password using the link: " + passwordChangeLink);
        return passwordChangeLink;
    }

    public void saveFirm(FirmDto firmRegisterRequest) {
        Firm firm = new Firm(firmRegisterRequest.getFirmName(), firmRegisterRequest.getGstNo(), firmRegisterRequest.getEmail(), firmRegisterRequest.getPhone());
        firmRepository.save(firm);
    }

    public void setPassword(String token, PasswordChange passwordChange) {
        if (token != null && jwtSetup.validateToken(token)) {
            String firmId = jwtSetup.extractClaim(token).get("firmId").toString();
            Firm firm = firmRepository.findById(firmId).orElseThrow(() -> new RuntimeException("Firm not found"));
            firm.setPassword(passwordChange.getNewPassword());
            firmRepository.save(firm);

        } else {
            throw new RuntimeException("Invalid token");
        }
    }
}
