package com.taxwiz.service.employee;

import com.taxwiz.dto.employee.EmployeeDto;
import com.taxwiz.dto.employee.EmployeeResponseDto;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.model.Employee;
import com.taxwiz.model.Role;
import com.taxwiz.model.User;
import com.taxwiz.repository.EmployeeRepository;
import com.taxwiz.repository.RoleRepository;
import com.taxwiz.repository.UserRepository;
import com.taxwiz.service.auth.JwtSetup;
import com.taxwiz.service.utils.PasswordResetService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.taxwiz.utils.ErrorMessages.NOT_FOUND;
import static com.taxwiz.utils.RoleInfo.FIRM_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetService passwordResetService;
    private final JwtSetup jwtSetup;

    public Employee createEmployee(EmployeeDto employeeDto, String token) {
        log.info("Creating employee");
        try {
            String adminUserName = jwtSetup.extractClaim(token, Claims::getSubject);
            User adminUser = userRepository.findByUsername(adminUserName);
            if (adminUser == null) {
                log.error("Admin user not found");
                throw new NotFoundException(NOT_FOUND.name());
            }
            if (!adminUser.isVerified()) {
                log.error("Admin user is not verified");
                throw new NotFoundException(NOT_FOUND.name());
            }

            User employeeUser = new User();
            Role firmUserRole = roleRepository.findByName(FIRM_USER.name());
            employeeUser.setUsername(employeeDto.getEmail());
            employeeUser.setVerified(false);
            employeeUser.setFirm(adminUser.getFirm());
            employeeUser.setRoles(List.of(firmUserRole));

            userRepository.save(employeeUser);
            log.info("Employee user created");
            passwordResetService.sendPasswordResetEmail(employeeDto.getEmail());
            log.info("Password reset email sent to: {}", employeeDto.getEmail());
            Employee employee = new Employee();
            employee.setAddress(employeeDto.getAddress());
            employee.setEmail(employeeDto.getEmail());
            employee.setFirstName(employeeDto.getFirstName());
            employee.setLastName(employeeDto.getLastName());
            employee.setPhone(employee.getPhone());
            employee.setUser(employeeUser);

            log.info("Saving employee");

            employeeRepository.save(employee);
            return employee;
        } catch (Exception e) {
            log.error("Error creating employee: {}", e.getMessage());
            throw new RuntimeException("Error creating employee");
        }
    }

    public List<EmployeeResponseDto> getAllVerifiedEmployees(String token) {
        log.info("Getting all verified employees");
        Long firmId = adminCheck(token);
        return employeeRepository
                .findByFirmId(firmId).stream()
                .filter(employee -> employee.getUser().isVerified())
                .map(employee -> new EmployeeResponseDto(employee.getUid(), employee.getFirstName(), employee.getLastName(), employee.getEmail(), employee.getPhone(), employee.getAddress()))
                .toList();
    }

    public List<EmployeeResponseDto> getAllUnverifiedEmployees(String token) {
        log.info("Getting all unverified employees");
        Long firmId = adminCheck(token);
        return employeeRepository
                .findByFirmId(firmId).stream()
                .filter(employee -> !employee.getUser().isVerified())
                .map(employee -> new EmployeeResponseDto(employee.getUid(), employee.getFirstName(), employee.getLastName(), employee.getEmail(), employee.getPhone(), employee.getAddress()))
                .toList();
    }

    public Employee updateEmployeeEmail(String uid, String email, String token) {
        log.info("Updating employee email for UID: {}", uid);
        Long firmId = adminCheck(token);
        Employee employee = employeeRepository.findByUidAndFirmId(uid, firmId);
        if (employee == null) {
            log.error("Employee not found");
            throw new NotFoundException(NOT_FOUND.name());
        }
        employee.getUser().setUsername(email);
        employee.getUser().setVerified(false);
        userRepository.save(employee.getUser());
        return employeeRepository.save(employee);
    }

public Employee updateEmployeePhone(String uid, String phone, String token) {
        log.info("Updating employee phone for UID: {}", uid);
        Long firmId = adminCheck(token);
        Employee employee = employeeRepository.findByUidAndFirmId(uid, firmId);
        if (employee == null) {
            log.error("Employee not found");
            throw new NotFoundException(NOT_FOUND.name());
        }
        employee.setPhone(phone);
        return employeeRepository.save(employee);
    }

public Employee updateEmployeeAddress(String uid, String address, String token) {
        log.info("Updating employee address for UID: {}", uid);
        Long firmId = adminCheck(token);
        Employee employee = employeeRepository.findByUidAndFirmId(uid, firmId);
        if (employee == null) {
            log.error("Employee not found");
            throw new NotFoundException(NOT_FOUND.name());
        }
        employee.setAddress(address);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployeeFirstName(String uid, String firstName, String token) {
        log.info("Updating employee first name for UID: {}", uid);
        Long firmId = adminCheck(token);
        Employee employee = employeeRepository.findByUidAndFirmId(uid, firmId);
        if (employee == null) {
            log.error("Employee not found");
            throw new NotFoundException(NOT_FOUND.name());
        }
        employee.setFirstName(firstName);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployeeLastName(String uid, String lastName, String token) {
        log.info("Updating employee last name for UID: {}", uid);
        Long firmId = adminCheck(token);
        Employee employee = employeeRepository.findByUidAndFirmId(uid, firmId);
        if (employee == null) {
            log.error("Employee not found");
            throw new NotFoundException(NOT_FOUND.name());
        }
        employee.setLastName(lastName);
        return employeeRepository.save(employee);
    }

    private Long adminCheck(String token) {
        String adminUserName = jwtSetup.extractClaim(token, Claims::getSubject);
        User adminUser = userRepository.findByUsername(adminUserName);
        if (adminUser == null) {
            log.error("Admin user not found");
            throw new NotFoundException(NOT_FOUND.name());
        }
        if (!adminUser.isVerified()) {
            log.error("Admin user is not verified");
            throw new NotFoundException(NOT_FOUND.name());
        }

        return adminUser.getFirm().getId();
    }
}
