package com.taxwiz.service.firm;

import com.taxwiz.dto.FirmDto;
import com.taxwiz.exception.AlreadyExistsException;
import com.taxwiz.model.Firm;
import com.taxwiz.model.Role;
import com.taxwiz.model.User;
import com.taxwiz.repository.FirmRepository;
import com.taxwiz.repository.RoleRepository;
import com.taxwiz.repository.UserRepository;
import com.taxwiz.service.utils.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.taxwiz.utils.ErrorMessages.ALREADY_EXISTS;
import static com.taxwiz.utils.RoleInfo.FIRM_ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirmRegistrationService {

    private final FirmRepository firmRepository;
    private final PasswordResetService passwordResetService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    /**
     * Registers a new firm in the system.
     *
     * @param firmDto the firm data transfer object containing firm details
     * @return the registered Firm object
     * @throws AlreadyExistsException if a firm with the same GST number already exists
     * @throws RuntimeException - if there is an error sending mail, generating token
     *
     * @implNote This method registers a firm, creates a new Admin user for the firm and sends Password generation mail to the admin user
     *
     */

    // TODO: Handle Email Failure

    public Firm registerFirm(FirmDto firmDto) throws AlreadyExistsException {
        log.info("Registering firm: {}", firmDto.getName());

        Firm firm = firmRepository.findByGstNo(firmDto.getGstNo());
        if (firm != null) {
            throw new AlreadyExistsException(ALREADY_EXISTS.name());
        }
        firm = new Firm(firmDto.getName(), firmDto.getGstNo(), firmDto.getAddress(), firmDto.getEmail(), firmDto.getPhone());
        Firm savedFirm = firmRepository.save(firm);
        log.info("Firm registered successfully: {}", firm.getName());

        Role admin = roleRepository.findByName(FIRM_ADMIN.name());
        List<Role> roles = List.of(admin);

        User user = new User();
        user.setFirm(savedFirm);
        user.setUsername(firmDto.getEmail());
        user.setRoles(roles);
        user.setVerified(false);
        userRepository.save(user);

        log.info("Firm Admin created successfully");

        passwordResetService.sendPasswordResetEmail(firmDto.getEmail());
        log.info("Password reset email sent successfully to: {}", firmDto.getEmail());

        return savedFirm;
    }
}
