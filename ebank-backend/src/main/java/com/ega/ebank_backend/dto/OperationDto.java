package com.ega.ebank_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OperationDto {
    @NotBlank(message = "Le numéro de compte est obligatoire")
    private String numCompte;
    private String numCompteDest;
    @Positive(message  = "Le montant doit être strictement supérieur à 0")
    private double montant;
    private String description;
}
