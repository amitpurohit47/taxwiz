package com.taxwiz.service.client;

import com.taxwiz.exception.AlreadyExistsException;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.exception.UnverifiedException;
import com.taxwiz.model.Client;
import com.taxwiz.model.Employee;
import com.taxwiz.model.User;
import com.taxwiz.repository.ClientRepository;
import com.taxwiz.repository.EmployeeRepository;
import com.taxwiz.repository.UserRepository;
import com.taxwiz.service.auth.JwtSetup;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.taxwiz.utils.ErrorMessages.ALREADY_EXISTS;
import static com.taxwiz.utils.ErrorMessages.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignClientService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final JwtSetup jwtSetup;

    /**
     * Assigning a client to employee
     *
     * @param clientUid   Client UID
     * @param employeeUid Employee UID
     * @param token       JWT token
     * @apiNote - This method will assign a client to an employee
     * @Scenario 1. Client Does not exist
     * 2. Employee Does not exist
     * 3. Client is already assigned to employee
     * 4. Employee does not belong to the firm
     * 5. Client does not belong to the firm
     * 6. Employee not verified
     * 7. Client not verified
     * 8. Token Invalid
     * 9. Token Expired
     * 10. Client assigned to employee successfully
     */
    public void assignClientToEmployee(String clientUid, String employeeUid, String token) {

        Client client = clientRepository.findByUid(clientUid);
        Employee employee = employeeRepository.findByUid(employeeUid);

        String adminUsername = jwtSetup.extractClaim(token, Claims::getSubject);
        User adminUser = userRepository.findByUsername(adminUsername);

        if (client == null || employee == null) {
            log.warn("Client {} or Employee {} does not exist", clientUid, employeeUid);
            throw new NotFoundException();
        }

        if (!client.isVerified() || !employee.getUser().isVerified()) {
            log.warn("Client {} or Employee {} is not verified", clientUid, employeeUid);
            throw new UnverifiedException();
        }

        if (!employee.getUser().getFirm().getUid().equals(adminUser.getFirm().getUid())) {
            log.warn("Employee {} does not belong to the firm {}", employeeUid, adminUser.getFirm().getUid());
            throw new NotFoundException(NOT_FOUND.name());
        }

        if (!client.getFirm().getUid().equals(adminUser.getFirm().getUid())) {
            log.warn("Client {} does not belong to the firm {}", clientUid, adminUser.getFirm().getUid());
            throw new NotFoundException(NOT_FOUND.name());
        }

        if (client.getEmployee() != null) {
            log.warn("Client {} is already assigned to employee {}", clientUid, client.getEmployee().getUid());
            throw new AlreadyExistsException();
        }

        client.setEmployee(employee);
        employee.getClients().add(client);
        clientRepository.save(client);
        employeeRepository.save(employee);
        log.info("Client {} assigned to employee {} successfully", clientUid, employeeUid);

    }

}
