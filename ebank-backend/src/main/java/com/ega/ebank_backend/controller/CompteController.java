package com.ega.ebank_backend.controller;

import com.ega.ebank_backend.dto.CompteDto;
import com.ega.ebank_backend.entities.*;
import com.ega.ebank_backend.repositories.ClientRepository;
import com.ega.ebank_backend.services.CompteService;
import com.ega.ebank_backend.services.OperationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comptes")
// @CrossOrigin("*") //
public class CompteController {
    private final CompteService compteService;
    private final ClientRepository clientRepository;
    private final OperationService operationService;

//    public CompteController(CompteService cs, ClientRepository cr, OperationService ops) {
//        this.compteService = cs;
//        this.clientRepository = cr;
//        this.operationService = ops;
//    }


    @GetMapping
    public List<CompteBancaire> listAll() {
        return compteService.getAllAccounts();
    }

    @GetMapping("/me")
    public List<CompteBancaire> getMyAccounts(Principal principal) {
        //  contient l'email de l'utilisateur connecté via le JWT
        String email = principal.getName();
        // chercher tous les comptes qui appartiennent à cet email
        return compteService.getAllAccounts().stream()
                .filter(c -> c.getClient().getEmail().equals(email))
                .toList();
    }

    @PostMapping
    public void create(@Valid @RequestBody CompteDto dto) { compteService.createCompte(dto); }

    @GetMapping("/courants")
    public List<CompteCourant> listCourants() { return compteService.findCompteCourant(); }

    @GetMapping("/epargnes")
    public List<CompteEpargne> listEpargnes() { return compteService.findCompteEpargnes(); }

    @GetMapping("/{numCompte}")
    public CompteBancaire getOne(@PathVariable String numCompte) { return compteService.findOne(numCompte); }

    @PutMapping("/status/{numCompte}")
    public void changeStatus(@PathVariable String numCompte, @RequestBody Map<String, String> request) {
        compteService.updateStatus(numCompte, request.get("status"));
    }

    @GetMapping("/historique/{numCompte}")
    public List<Operation> getHistoriqueClient(@PathVariable String numCompte, Principal principal) {
        CompteBancaire cpte = compteService.findOne(numCompte);
        // Vérifier que le compte appartient bien au client connecté
        if (!cpte.getClient().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Accès refusé à cet historique");
        }
        return operationService.consulterHistorique(numCompte);
    }
}