package com.taxwiz.repository.firm;

import com.taxwiz.model.Firm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmRepository extends JpaRepository<Firm, String> {
}
