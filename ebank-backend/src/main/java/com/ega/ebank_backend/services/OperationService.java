package com.ega.ebank_backend.services;

import com.ega.ebank_backend.entities.CompteBancaire;
import com.ega.ebank_backend.entities.Operation;
import com.ega.ebank_backend.enums.TypeOperation;
import java.util.Date;
import java.util.List;

public interface OperationService {
    void effectuerVersement(String numCompte, double montant, String description);
    void effectuerRetrait(String numCompte, double montant, String description);
    void effectuerVirement(String numSrc, String numDest, double montant, String description);
    void enregistrerOp(CompteBancaire cpte, double mt, TypeOperation type, String desc); // Signature propre
    List<Operation> consulterHistoriqueParPeriode(String numCompte, Date debut, Date fin);
    List<Operation> consulterHistorique(String numCompte);
}