package com.ega.ebank_backend.controller;

import com.ega.ebank_backend.dto.OperationDto;
import com.ega.ebank_backend.entities.CompteBancaire;
import com.ega.ebank_backend.entities.Operation;
import com.ega.ebank_backend.services.CompteService;
import com.ega.ebank_backend.services.OperationService;
import com.ega.ebank_backend.services.PdfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operations")

public class OperationController {

    @Autowired private OperationService operationService;
    @Autowired private PdfService pdfService;
    @Autowired private CompteService compteService;

    @PostMapping("/versement")
    public void versement(@Valid @RequestBody OperationDto dto) {
        operationService.effectuerVersement(dto.getNumCompte(), dto.getMontant(), dto.getDescription());
    }

    @PostMapping("/retrait")
    public void retrait(@Valid @RequestBody OperationDto dto) {
        operationService.effectuerRetrait(dto.getNumCompte(), dto.getMontant(), dto.getDescription());
    }

    @PostMapping("/virement")
    public void virement(@Valid @RequestBody OperationDto dto) {
        operationService.effectuerVirement(dto.getNumCompte(), dto.getNumCompteDest(), dto.getMontant(), dto.getDescription());
    }

    @GetMapping("/historique-periode/{numCompte}")
    public List<Operation> getHistoriqueParPeriode(
            @PathVariable String numCompte,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date debut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fin) {
        return operationService.consulterHistoriqueParPeriode(numCompte, debut, fin);
    }
    @GetMapping("/historique/{numCompte}")
    public List<Operation> getHistorique(@PathVariable String numCompte) {
        return operationService.consulterHistorique(numCompte);
    }

    @GetMapping("/imprimer-releve/{numCompte}")
    public ResponseEntity<byte[]> imprimerReleve(
            @PathVariable String numCompte,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date debut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fin) {

        try {
            CompteBancaire compte = compteService.findOne(numCompte);
            List<Operation> operations = operationService.consulterHistoriqueParPeriode(numCompte, debut, fin);

            Map<String, Object> variables = new HashMap<>();
            variables.put("compte", compte);
            variables.put("client", compte.getClient());
            variables.put("operations", operations);

            // envoie le texte du type de compte directement
            String typeLabel = (compte.getClass().getSimpleName().equals("CompteCourant")) ? "Compte Courant" : "Compte Épargne";
            variables.put("typeCompte", typeLabel);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            variables.put("periode", "du " + sdf.format(debut) + " au " + sdf.format(fin));
            variables.put("dateEdition", sdf.format(new Date()));

            byte[] pdfContent = pdfService.genererPdf("releve_template", variables);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "releve_" + numCompte + ".pdf");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}