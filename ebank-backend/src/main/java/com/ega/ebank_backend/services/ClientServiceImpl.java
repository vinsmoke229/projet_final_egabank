package com.ega.ebank_backend.services;

import com.ega.ebank_backend.dto.ClientDto;
import com.ega.ebank_backend.entities.*;
import com.ega.ebank_backend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final AppUserRepository appUserRepository;
    private final CompteBancaireRepository compteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public List<Client> findAll() {
        return clientRepository.findByActiveTrue();
    }

    @Override
    public Client findOne(long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        //  charge les comptes associés pour l'affichage dans les détails
        client.setCompteBancaires(compteRepository.findByClientId(id));
        return client;
    }

    @Override
    public void createNewClient(ClientDto dto) {

        Optional<Client> existingClient = clientRepository.findByEmail(dto.getEmail());

        if (existingClient.isPresent()) {
            Client client = existingClient.get();
            if (!client.isActive()) {
                // RÉACTIVATION
                client.setActive(true);
                client.setNom(dto.getNom());
                client.setPrenom(dto.getPrenom());
                //  mettre à jour les autres champs
                clientRepository.save(client);

                // Réactiver  son AppUser
                appUserRepository.findByUsername(client.getEmail()).ifPresent(u -> {
                    u.setActive(true);
                    appUserRepository.save(u);
                });

                emailService.sendNotificationEmail(client.getEmail(), "EGA BANK - Réactivation", "Bon retour ! Votre compte a été réactivé.");
                return;
            } else {
                throw new RuntimeException("Ce client possède déjà un compte actif.");
            }
        }
        if(clientRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }
        Client client = new Client();
        mapDtoToEntity(dto, client);
        client.setActive(true);
        clientRepository.save(client);

        String tempPass = UUID.randomUUID().toString().substring(0, 8);
        AppUser user = new AppUser(null, client.getEmail(), passwordEncoder.encode(tempPass), "USER", true);
        appUserRepository.save(user);

        emailService.sendNotificationEmail(client.getEmail(),
                "Bienvenue chez EGA BANK",
                "Vos identifiants :\nLogin : " + client.getEmail() + "\nMot de passe : " + tempPass);
    }

    @Override
    public Client updateClient(Long id, ClientDto dto) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client introuvable"));
        String oldEmail = client.getEmail();

        // MISE À JOUR
        mapDtoToEntity(dto, client);

        if (!oldEmail.equals(dto.getEmail())) {
            appUserRepository.findByUsername(oldEmail).ifPresent(user -> {
                user.setUsername(dto.getEmail());
                appUserRepository.save(user);
            });
        }
        return clientRepository.save(client);
    }

    private void mapDtoToEntity(ClientDto dto, Client client) {
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setTelephone(dto.getTelephone());
        client.setAdresse(dto.getAdresse());
        client.setNationalite(dto.getNationalite());
        client.setSexe(dto.getSexe());
        client.setDateNaissance(dto.getDateNaissance());
    }

    @Override
    public void deleteClient(Long id) {
        Client client = findOne(id);
        client.setActive(false);
        clientRepository.save(client);
        appUserRepository.findByUsername(client.getEmail()).ifPresent(u -> {
            u.setActive(false);
            appUserRepository.save(u);
        });
    }
}