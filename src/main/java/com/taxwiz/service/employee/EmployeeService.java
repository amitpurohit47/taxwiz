package com.taxwiz.service.employee;

import com.taxwiz.dto.employee.EmployeeDto;
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
}
