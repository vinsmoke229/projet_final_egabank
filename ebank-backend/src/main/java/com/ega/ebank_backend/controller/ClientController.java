package com.ega.ebank_backend.controller;

import com.ega.ebank_backend.dto.ClientDto;
import com.ega.ebank_backend.entities.Client;
import com.ega.ebank_backend.services.ClientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin("*")
@AllArgsConstructor
public class ClientController {
    private final ClientService clientService;
//    public ClientController(ClientService cs) { this.clientService = cs; }

    @PostMapping
    public void save(@Valid @RequestBody ClientDto dto) { clientService.createNewClient(dto); }

    @GetMapping
    public List<Client> list() { return clientService.findAll(); }

    @GetMapping("/{id}")
    public Client getOne(@PathVariable Long id) { return clientService.findOne(id); }

    @PutMapping("/{id}")
    public Client update(@PathVariable Long id, @Valid @RequestBody ClientDto dto) {
        return clientService.updateClient(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}