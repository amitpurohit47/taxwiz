package com.taxwiz.repository;

import com.taxwiz.model.Client;
import com.taxwiz.model.Firm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByUid(String uid);
    List<Client> findAllByFirmId(Long firmId);
}
