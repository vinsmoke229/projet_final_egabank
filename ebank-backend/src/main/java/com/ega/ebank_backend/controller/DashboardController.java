package com.ega.ebank_backend.controller;

import com.ega.ebank_backend.entities.Operation;
import com.ega.ebank_backend.repositories.ClientRepository;
import com.ega.ebank_backend.repositories.CompteBancaireRepository;
import com.ega.ebank_backend.repositories.OperationRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ClientRepository clientRepo;
    private final CompteBancaireRepository compteRepo;
    private final OperationRepository opRepo;

    public DashboardController(ClientRepository clientRepo, CompteBancaireRepository compteRepo, OperationRepository opRepo) {
        this.clientRepo = clientRepo;
        this.compteRepo = compteRepo;
        this.opRepo = opRepo;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClients", clientRepo.count());
        stats.put("totalComptes", compteRepo.count());
        stats.put("totalSolde", compteRepo.findAll().stream().mapToDouble(c -> c.getSolde()).sum());

        //  récupère 5 dernières transactions pour le dashboard
        List<Operation> recentOps = opRepo.findAll().stream()
                .sorted(Comparator.comparing(Operation::getDateOperation).reversed())
                .limit(5).toList();
        stats.put("recentTransactions", recentOps);

        return stats;
    }
}