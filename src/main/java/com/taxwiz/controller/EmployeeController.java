package com.taxwiz.controller;

import com.taxwiz.dto.EmployeeDto;
import com.taxwiz.dto.EmployeeResponseDto;
import com.taxwiz.dto.ErrorResponseDto;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.model.Employee;
import com.taxwiz.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.taxwiz.utils.ErrorMessages.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('FIRM_ADMIN')")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDto employeeDto, @RequestHeader("Authorization") String authorization) {
        log.info("Creating employee");
        try {
            String token = authorization.substring(7);
            Employee createdEmployee = employeeService.createEmployee(employeeDto, token);
            EmployeeResponseDto employeeResponseDto = getEmployeeResponseDto(createdEmployee);
            return ResponseEntity.ok(employeeResponseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    private EmployeeResponseDto getEmployeeResponseDto(Employee createdEmployee) {
        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();
        employeeResponseDto.setUid(createdEmployee.getUid());
        employeeResponseDto.setAddress(createdEmployee.getAddress());
        employeeResponseDto.setEmail(createdEmployee.getEmail());
        employeeResponseDto.setPhone(createdEmployee.getPhone());
        employeeResponseDto.setFirstName(createdEmployee.getFirstName());
        employeeResponseDto.setLastName(createdEmployee.getLastName());
        return employeeResponseDto;
    }
}
