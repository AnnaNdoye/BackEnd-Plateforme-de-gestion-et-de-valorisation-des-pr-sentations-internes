package com.example.departement.dto;

import java.time.LocalDateTime;

import com.example.departement.entity.Document;

public class DocumentDTO {
    private Integer idDocument;
    private Integer idPresentation;
    private String nom;
    private String chemin;
    private String description;
    private LocalDateTime dateDeSoumission;

    // Constructeur par défaut
    public DocumentDTO() {}

    // Constructeur à partir d'une entité Document
    public DocumentDTO(Document document) {
        this.idDocument = document.getIdDocument();
        this.idPresentation = document.getPresentation().getIdPresentation();
        this.nom = document.getNom();
        this.chemin = document.getChemin();
        this.description = document.getDescription();
        this.dateDeSoumission = document.getDateDeSoumission();
    }

    // Getters et Setters
    public Integer getIdDocument() { return idDocument; }
    public void setIdDocument(Integer idDocument) { this.idDocument = idDocument; }

    public Integer getIdPresentation() { return idPresentation; }
    public void setIdPresentation(Integer idPresentation) { this.idPresentation = idPresentation; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getChemin() { return chemin; }
    public void setChemin(String chemin) { this.chemin = chemin; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateDeSoumission() { return dateDeSoumission; }
    public void setDateDeSoumission(LocalDateTime dateDeSoumission) { this.dateDeSoumission = dateDeSoumission; }
}
