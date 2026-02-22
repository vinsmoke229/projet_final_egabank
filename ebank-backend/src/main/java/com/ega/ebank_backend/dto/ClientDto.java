package com.ega.ebank_backend.dto;

import com.ega.ebank_backend.enums.Sexe;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class ClientDto {
    @NotBlank(message = "Le nom est obligatoire")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom ne doit contenir que des lettres")

    private String nom;

   @NotBlank(message = "Le prénom est obligatoire")
   @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le prénom ne doit contenir que des lettres")

   private String prenom;

    @NotNull(message = "la date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être une date passée")
    private Date dateNaissance;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le sexe est obligatoire")
    private Sexe sexe;

    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")

    private String email;

    private String nationalite;

}
