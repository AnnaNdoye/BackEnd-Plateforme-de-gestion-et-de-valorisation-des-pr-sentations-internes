package com.example.departement.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Integer idDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presentation", nullable = false)
    @JsonIgnoreProperties({"documents", "votes", "commentaires", "utilisateur"})
    private Presentation presentation;

    @Column(name = "nom", nullable = false)
    @NotBlank(message = "Le nom du document est obligatoire")
    private String nom;

    @Column(name = "chemin", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Le chemin du document est obligatoire")
    private String chemin;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_de_soumission")
    private LocalDateTime dateDeSoumission;

    @PrePersist
    protected void onCreate() {
        dateDeSoumission = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getIdDocument() { return idDocument; }
    public void setIdDocument(Integer idDocument) { this.idDocument = idDocument; }
    
    public Presentation getPresentation() { return presentation; }
    public void setPresentation(Presentation presentation) { this.presentation = presentation; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getChemin() { return chemin; }
    public void setChemin(String chemin) { this.chemin = chemin; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getDateDeSoumission() { return dateDeSoumission; }
    public void setDateDeSoumission(LocalDateTime dateDeSoumission) { this.dateDeSoumission = dateDeSoumission; }
}