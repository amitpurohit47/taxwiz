package com.taxwiz.repository;

import com.taxwiz.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUid(String uid);
    Employee findByUid(String uid);
    @Query("SELECT e FROM Employee e JOIN e.user u WHERE u.firm.id = :firmId")
    List<Employee> findByFirmId(Long firmId);
    @Query("SELECT e FROM Employee e JOIN e.user u WHERE u.firm.id = :firmId AND u.verified = true")
    List<Employee> findVerifiedByFirmId(Long firmId);
    // findBuUidAndFirmId
    @Query("SELECT e FROM Employee e JOIN e.user u WHERE e.uid = :uid AND u.firm.id = :firmId")
    Employee findByUidAndFirmId(String uid, Long firmId);

}
