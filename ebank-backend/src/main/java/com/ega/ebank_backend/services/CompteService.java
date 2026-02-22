package com.ega.ebank_backend.services;

import com.ega.ebank_backend.dto.CompteDto;
import com.ega.ebank_backend.entities.CompteBancaire;
import com.ega.ebank_backend.entities.CompteCourant;
import com.ega.ebank_backend.entities.CompteEpargne;

import java.util.List;

public interface CompteService {
    void createCompte(CompteDto compteDto);

    List<CompteBancaire> getAllAccounts();

    List<CompteEpargne> findCompteEpargnes();
    List<CompteCourant> findCompteCourant();
    CompteBancaire findOne(String numCompte);
    void updateStatus(String numCompte, String status);

    // Récupérer les comptes par identifiant client
    List<CompteBancaire> findByClientId(Long clientId);
}