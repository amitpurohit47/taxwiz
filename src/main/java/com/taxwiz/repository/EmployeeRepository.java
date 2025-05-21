package com.taxwiz.repository;

import com.taxwiz.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUid(String uid);
    Employee findByUid(String uid);
    List<Employee> findByFirmId(Long firmId);
}
