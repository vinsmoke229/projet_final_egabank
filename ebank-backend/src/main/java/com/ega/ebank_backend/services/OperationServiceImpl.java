package com.ega.ebank_backend.services;

import com.ega.ebank_backend.entities.*;
import com.ega.ebank_backend.enums.AccountStatus;
import com.ega.ebank_backend.enums.TypeOperation;
import com.ega.ebank_backend.repositories.CompteBancaireRepository;
import com.ega.ebank_backend.repositories.OperationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OperationServiceImpl implements OperationService {
    private final OperationRepository operationRepository;
    private final CompteBancaireRepository compteRepository;
    private final EmailService emailService;

    public OperationServiceImpl(OperationRepository opRepo, CompteBancaireRepository cpteRepo, EmailService emailService) {
        this.operationRepository = opRepo;
        this.compteRepository = cpteRepo;
        this.emailService = emailService;
    }

    @Override
    public void effectuerVersement(String numCompte, double montant, String description) {
        CompteBancaire cpte = findCompte(numCompte);
        if (cpte.getStatus() != AccountStatus.ACTIVATED) throw new RuntimeException("Compte non actif");

        cpte.setSolde(cpte.getSolde() + montant);
        compteRepository.save(cpte);
        String desc = description != null ? description : "Versement espèces";
        enregistrerOp(cpte, montant, TypeOperation.CREDIT, desc);

        notifierClient(cpte, montant, "CRÉDIT (Versement)", desc);
    }

    @Override
    public void effectuerRetrait(String numCompte, double montant, String description) {
        CompteBancaire cpte = findCompte(numCompte);
        if (cpte.getStatus() != AccountStatus.ACTIVATED) throw new RuntimeException("Compte non actif");

        double limite = (cpte instanceof CompteCourant) ? -((CompteCourant) cpte).getDecouvert() : 0;
        if (cpte.getSolde() - montant < limite) throw new RuntimeException("Solde insuffisant");

        cpte.setSolde(cpte.getSolde() - montant);
        compteRepository.save(cpte);
        String desc = description != null ? description : "Retrait espèces";
        enregistrerOp(cpte, montant, TypeOperation.DEBIT, desc);

        notifierClient(cpte, montant, "DÉBIT (Retrait)", desc);
    }

    @Override
    public void effectuerVirement(String numSrc, String numDest, double montant, String description) {
        effectuerRetrait(numSrc, montant, "Virement vers " + numDest + " : " + description);
        effectuerVersement(numDest, montant, "Virement reçu de " + numSrc + " : " + description);
    }

    @Override
    public void enregistrerOp(CompteBancaire cpte, double mt, TypeOperation type, String desc) {
        Operation op = new Operation();
        op.setMontant(mt);
        op.setDescription(desc);
        op.setDateOperation(new Date());
        op.setTypeOperation(type);
        op.setNumOperation("OP-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        op.setCompteBancaire(cpte);
        op.setSoldeApres(cpte.getSolde());
        operationRepository.save(op);
    }

    private void notifierClient(CompteBancaire cpte, double montant, String typeLabel, String motif) {
        String recipient = cpte.getClient().getEmail();
        String subject = "Avis d'opération - EGA BANK";
        String content = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee; border-radius: 10px; max-width: 600px;'>" +
                "<h2 style='color: #0056b3;'>EGA BANK</h2>" +
                "<p>Bonjour <strong>" + cpte.getClient().getPrenom() + " " + cpte.getClient().getNom() + "</strong>,</p>" +
                "<p>Une nouvelle opération a été enregistrée sur votre compte.</p>" +
                "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<p style='margin: 5px 0;'><strong>Type d'opération :</strong> " + typeLabel + "</p>" +
                "<p style='margin: 5px 0;'><strong>Compte :</strong> " + cpte.getNumCompte() + "</p>" +
                "<p style='margin: 5px 0;'><strong>Montant :</strong> " + String.format("%.2f", montant) + " CFA</p>" +
                "<p style='margin: 5px 0;'><strong>Libellé :</strong> " + motif + "</p>" +
                "<p style='margin: 5px 0;'><strong>Nouveau Solde :</strong> <span style='color: #0056b3; font-weight: bold;'>" + String.format("%.2f", cpte.getSolde()) + " CFA</span></p>" +
                "</div>" +
                "<p style='font-size: 12px; color: #777;'>Ceci est un message automatique, merci de ne pas y répondre.</p>" +
                "</div>";
        emailService.sendNotificationEmail(recipient, subject, content);
    }

    @Override
    public List<Operation> consulterHistoriqueParPeriode(String numCompte, Date debut, Date fin) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fin);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return operationRepository.findByCompteBancaireNumCompteAndDateOperationBetween(numCompte, debut, cal.getTime());
    }

    @Override
    public List<Operation> consulterHistorique(String numCompte) {
        return operationRepository.findByCompteBancaireNumCompteOrderByDateOperationDesc(numCompte);
    }

    private CompteBancaire findCompte(String num) {
        return compteRepository.findByNumCompte(num).orElseThrow(() -> new RuntimeException("Compte introuvable"));
    }
}