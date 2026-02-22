package com.ega.ebank_backend.repositories;

import com.ega.ebank_backend.entities.CompteBancaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompteBancaireRepository extends JpaRepository<CompteBancaire, Long> {

    @Query("SELECT c FROM CompteBancaire c JOIN FETCH c.client LEFT JOIN FETCH c.operations WHERE c.numCompte = :num")
    Optional<CompteBancaire> findByNumCompte(@Param("num") String num);

    @Query("SELECT DISTINCT c FROM CompteBancaire c JOIN FETCH c.client WHERE c.client.active = true ORDER BY c.id DESC")
    List<CompteBancaire> findAllAccountsWithClient();

    @Query("SELECT c FROM CompteBancaire c JOIN FETCH c.client WHERE c.client.active = true ORDER BY c.id DESC")
    List<CompteBancaire> findAllActiveAccounts();

    List<CompteBancaire> findByClientId(Long clientId);
}