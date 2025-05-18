package com.taxwiz.controller;

import com.taxwiz.dto.ErrorResponseDto;
import com.taxwiz.dto.role.RoleDto;
import com.taxwiz.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<?> createRole(@RequestBody RoleDto roleDto) {
        log.info("Creating role: {}",roleDto.getName());
        try {
            roleService.createRole(roleDto);
            log.info("Role created successfully: {}", roleDto.getName());
            return ResponseEntity.ok("Role created Successfully");
        } catch (Exception e) {
            log.error("Error while creating role: {}", roleDto.getName());
            return ResponseEntity.status(500).body(new ErrorResponseDto(e.getMessage()));
        }
    }
}
