package com.taxwiz.service.role;

import com.taxwiz.dto.role.RoleDto;
import com.taxwiz.exception.AlreadyExistsException;
import com.taxwiz.model.Role;
import com.taxwiz.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.taxwiz.utils.ErrorMessages.ALREADY_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public void createRole(RoleDto roleDto) {

        if (roleRepository.findByName(roleDto.getName()) != null) {
            log.error("Role {} already exists", roleDto.getName());
            throw new AlreadyExistsException(ALREADY_EXISTS.name());
        }

        Role role = new Role();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        Role createdRole = null;
        try {
            createdRole = roleRepository.save(role);
        } catch (Exception e) {
            log.error("Error while creating role");
        }
    }
}
