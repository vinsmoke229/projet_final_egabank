package com.ega.ebank_backend.entities;

import com.ega.ebank_backend.enums.TypeOperation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operation implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double montant;

    private String description;

    @Column(nullable=false, unique = true)
    private String numOperation;

    @Column(nullable=false)
    private Date dateOperation;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TypeOperation typeOperation;

    @ManyToOne
    @JoinColumn(name = "compte_id")
    private CompteBancaire compteBancaire;


    private double soldeApres;
}