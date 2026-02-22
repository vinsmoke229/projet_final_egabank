package com.ega.ebank_backend.entities;

import com.ega.ebank_backend.enums.Sexe;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateNaissance;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;
    private String adresse;
    private String telephone;

    @Column(unique = true)
    private String email;
    private String nationalite;
    private boolean active = true;


    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("client")
    private Collection<CompteBancaire> compteBancaires;
}