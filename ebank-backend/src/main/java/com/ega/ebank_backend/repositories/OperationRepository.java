package com.ega.ebank_backend.repositories;

import com.ega.ebank_backend.entities.Operation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByCompteBancaireNumCompteAndDateOperationBetween(String numCompte, Date dateDebut, Date dateFin);
    List<Operation> findByCompteBancaireNumCompteOrderByDateOperationDesc(String numCompte);
    @Query("SELECT o FROM Operation o JOIN FETCH o.compteBancaire cb JOIN FETCH cb.client ORDER BY o.dateOperation DESC")
    List<Operation> findAllWithClientDetails();
}
