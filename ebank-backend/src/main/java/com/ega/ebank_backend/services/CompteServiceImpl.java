package com.ega.ebank_backend.services;

import com.ega.ebank_backend.dto.CompteDto;
import com.ega.ebank_backend.entities.*;
import com.ega.ebank_backend.enums.AccountStatus;
import com.ega.ebank_backend.repositories.ClientRepository;
import com.ega.ebank_backend.repositories.CompteBancaireRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CompteServiceImpl implements CompteService {
    private final CompteBancaireRepository compteRepository;
    private final ClientRepository clientRepository;
    private final EmailService emailService;

//    public CompteServiceImpl(CompteBancaireRepository repo, ClientRepository clientRepo) {
//        this.compteRepository = repo;
//        this.clientRepository = clientRepo;
//    }

    @Override
    public void createCompte(CompteDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client inexistant"));

        CompteBancaire compte;
        String typeLabel = "";
        double vDecouvert = (dto.getDecouvert() != null) ? dto.getDecouvert() : 0.0;
        double vTaux = (dto.getTauxInteret() != null) ? dto.getTauxInteret() : 0.0;

        if (vDecouvert > 0) {
            CompteCourant cc = new CompteCourant();
            cc.setDecouvert(vDecouvert);
            compte = cc;
            typeLabel = "COURANT";
        } else {
            CompteEpargne ce = new CompteEpargne();
            ce.setTauxInteret(vTaux);
            compte = ce;
            typeLabel = "ÉPARGNE";
        }

        compte.setNumCompte(Iban.random(CountryCode.FR).toString());
        compte.setSolde(0);
        compte.setCreatedAt(new Date());
        compte.setStatus(AccountStatus.ACTIVATED);
        compte.setClient(client);
        compte.setDevis(dto.getDevis() != null ? dto.getDevis(): "CFA");

        CompteBancaire savedCompte = compteRepository.save(compte);
        notifierOuverture(client, savedCompte, typeLabel);
    }


    private void notifierOuverture(Client client, CompteBancaire compte, String type) {
        String sujet = "Ouverture de compte - EGA BANK";
        String corps = "<div style='font-family: Arial, sans-serif; border: 1px solid #0056b3; padding: 25px; border-radius: 15px; max-width: 600px;'>" +
                "<h2 style='color: #0056b3; text-align: center;'>EGA BANK</h2>" +
                "<p>Bonjour <b>" + client.getPrenom() + " " + client.getNom() + "</b>,</p>" +
                "<p>Nous avons le plaisir de vous informer que votre nouveau compte bancaire a été ouvert avec succès.</p>" +
                "<div style='background-color: #f4f7f6; padding: 20px; border-radius: 10px; margin: 20px 0; border-left: 5px solid #0056b3;'>" +
                "<p style='margin: 5px 0;'><b>Type de compte :</b> Compte " + type + "</p>" +
                "<p style='margin: 5px 0;'><b>Numéro de compte (IBAN) :</b> <code style='color: #d63384; font-weight: bold;'>" + compte.getNumCompte() + "</code></p>" +
                "<p style='margin: 5px 0;'><b>Solde initial :</b> 0,00 CFA</p>" +
                "<p style='margin: 5px 0;'><b>Statut :</b> ACTIF</p>" +
                "</div>" +
                "<p>Vous pouvez dès à présent effectuer votre premier dépôt en agence ou via nos services partenaires.</p>" +
                "<p style='font-size: 13px; color: #666; margin-top: 30px; border-top: 1px solid #eee; pt-10px;'>" +
                "Merci de votre confiance.<br/>L'équipe EGA BANK.</p>" +
                "</div>";

        emailService.sendNotificationEmail(client.getEmail(), sujet, corps);
    }

    @Override
    public List<CompteBancaire> getAllAccounts() {
        //  appelle la requête qui filtre les clients supprimés
        return compteRepository.findAllActiveAccounts();
    }

    @Override
    public List<CompteEpargne> findCompteEpargnes() {
        return compteRepository.findAll().stream()
                .filter(c-> c instanceof CompteEpargne)
                .map(c->(CompteEpargne) c).toList();
    }

    @Override
    public List<CompteCourant> findCompteCourant() {
        return compteRepository.findAll().stream()
                .filter(c -> c instanceof CompteCourant)
                .map(c -> (CompteCourant) c).toList();
    }

    @Override
    public CompteBancaire findOne(String numCompte) {
        //  appelle le nom findByNumCompte
        return compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new RuntimeException("Compte " + numCompte + " introuvable"));
    }

    @Override
    public void updateStatus(String numCompte, String status) {
        CompteBancaire compte = findOne(numCompte);
        try {
            // Convertirrr le string CLOSED ou closed en Enum de façon safe
            AccountStatus newStatus = AccountStatus.valueOf(status.toUpperCase());
            compte.setStatus(newStatus);


            compteRepository.save(compte);
            System.out.println("Succès: Compte " + numCompte + " passé à l'état " + newStatus);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide : " + status);
        }

    }

    @Override
    public List<CompteBancaire> findByClientId(Long clientId) {
        return compteRepository.findByClientId(clientId);
    }
}