package com.taxwiz.controller;

import com.taxwiz.dto.employee.EmployeeDto;
import com.taxwiz.dto.employee.EmployeeResponseDto;
import com.taxwiz.dto.ErrorResponseDto;
import com.taxwiz.exception.NotFoundException;
import com.taxwiz.model.Employee;
import com.taxwiz.service.employee.EmployeeService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/fetch")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> fetchAllEmployees(@RequestHeader("Authorization") String authorization) {
        log.info("Fetching all employees");
        try {
            String token = authorization.substring(7);
            List<EmployeeResponseDto> employeeResponse = employeeService.getAllVerifiedEmployees(token);
            return ResponseEntity.ok(employeeResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("/fetch/unverified")
    @PreAuthorize("hasAuthority('FIRM_ADMIN')")
    public ResponseEntity<?> fetchAllUnverifiedEmployees(@RequestHeader("Authorization") String authorization) {
        log.info("Fetching all unverified employees");
        try {
            String token = authorization.substring(7);
            List<EmployeeResponseDto> employeeResponse = employeeService.getAllUnverifiedEmployees(token);
            return ResponseEntity.ok(employeeResponse);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }

    }

    @PatchMapping("/update/firstname/{uid}")
    @PreAuthorize("hasRole('FIRM_ADMIN')")
    public ResponseEntity<?> updateEmployeeFirstName(@PathVariable String uid, @RequestParam String firstName, @RequestHeader("Authorization") String authorization) {
        log.info("Updating employee first name for UID: {}", uid);
        try {
            String token = authorization.substring(7);
            Employee updatedEmployee = employeeService.updateEmployeeFirstName(uid, firstName, token);
            EmployeeResponseDto employeeResponseDto = getEmployeeResponseDto(updatedEmployee);
            return ResponseEntity.ok(employeeResponseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/lastname/{uid}")
    @PreAuthorize("hasRole('FIRM_ADMIN')")
    public ResponseEntity<?> updateEmployeeLastName(@PathVariable String uid, @RequestParam String lastName, @RequestHeader("Authorization") String authorization) {
        log.info("Updating employee last name for UID: {}", uid);
        try {
            String token = authorization.substring(7);
            Employee updatedEmployee = employeeService.updateEmployeeLastName(uid, lastName, token);
            EmployeeResponseDto employeeResponseDto = getEmployeeResponseDto(updatedEmployee);
            return ResponseEntity.ok(employeeResponseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/email/{uid}")
    @PreAuthorize("hasRole('FIRM_ADMIN')")
    public ResponseEntity<?> updateEmployeeEmail(@PathVariable String uid, @RequestParam String email, @RequestHeader("Authorization") String authorization) {
        log.info("Updating employee email for UID: {}", uid);
        try {
            String token = authorization.substring(7);
            Employee updatedEmployee = employeeService.updateEmployeeEmail(uid, email, token);
            EmployeeResponseDto employeeResponseDto = getEmployeeResponseDto(updatedEmployee);
            return ResponseEntity.ok(employeeResponseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/phone/{uid}")
    @PreAuthorize("hasRole('FIRM_ADMIN')")
    public ResponseEntity<?> updateEmployeePhone(@PathVariable String uid, @RequestParam String phone, @RequestHeader("Authorization") String authorization) {
        log.info("Updating employee phone for UID: {}", uid);
        try {
            String token = authorization.substring(7);
            Employee updatedEmployee = employeeService.updateEmployeePhone(uid, phone, token);
            EmployeeResponseDto employeeResponseDto = getEmployeeResponseDto(updatedEmployee);
            return ResponseEntity.ok(employeeResponseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/update/address/{uid}")
    @PreAuthorize("hasRole('FIRM_ADMIN')")
    public ResponseEntity<?> updateEmployeeAddress(@PathVariable String uid, @RequestParam String address, @RequestHeader("Authorization") String authorization) {
        log.info("Updating employee address for UID: {}", uid);
        try {
            String token = authorization.substring(7);
            Employee updatedEmployee = employeeService.updateEmployeeAddress(uid, address, token);
            EmployeeResponseDto employeeResponseDto = getEmployeeResponseDto(updatedEmployee);
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
