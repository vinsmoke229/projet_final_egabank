package com.ega.ebank_backend.services;

import com.ega.ebank_backend.dto.ClientDto;
import com.ega.ebank_backend.entities.Client;
import java.util.List;

public interface ClientService {
    void createNewClient(ClientDto clientDto);
    List<Client> findAll();
    Client findOne(long id);
    Client updateClient(Long id, ClientDto clientDto);
    void deleteClient(Long id);
}