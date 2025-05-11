package com.taxwiz.repository;

import com.taxwiz.model.Firm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirmRepository extends JpaRepository<Firm, Long> {
    Firm findByGstNo(String gstNo);
}
