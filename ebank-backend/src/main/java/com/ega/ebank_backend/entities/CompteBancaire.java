package com.ega.ebank_backend.entities;

import com.ega.ebank_backend.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public abstract class CompteBancaire implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double solde;
    private String numCompte;
    private String devis = "CFA";

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private Date createdAt = new Date();


    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "compteBancaire", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("compteBancaire")
    private Collection<Operation> operations = new ArrayList<>();
}