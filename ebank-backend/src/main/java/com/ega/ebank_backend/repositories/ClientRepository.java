package com.ega.ebank_backend.repositories;

import com.ega.ebank_backend.entities.Client;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByActiveTrue();

    boolean existsByEmail(String email);

    Optional<Client> findByEmail(String email);
}