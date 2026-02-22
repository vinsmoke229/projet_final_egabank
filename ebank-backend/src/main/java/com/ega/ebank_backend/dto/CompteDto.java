package com.ega.ebank_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompteDto {
    private String devis = "CFA";
    private Double soldeInitial = 0.0;
    private Double decouvert = 0.0;
    private Double tauxInteret = 0.0;
    @NotNull(message = "L'ID du client est obligatoire pour créer un compte")
    private Long clientId;
}
