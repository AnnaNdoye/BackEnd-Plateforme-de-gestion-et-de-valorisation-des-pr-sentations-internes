package com.example.departement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "presentations")
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presentation")
    private Integer idPresentation;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "date_presentation", nullable = false)
    @NotNull(message = "La date de présentation est obligatoire")
    private LocalDate datePresentation;

    @Column(name = "heure_debut", nullable = false)
    @NotNull(message = "L'heure de début est obligatoire")
    private LocalDateTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalDateTime heureFin;

    @Column(name = "sujet", nullable = false)
    @NotBlank(message = "Le sujet est obligatoire")
    @Size(min = 2, max = 255, message = "Le sujet doit contenir entre 2 et 255 caractères")
    private String sujet;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    @NotNull(message = "Le statut est obligatoire")
    private StatutPresentation statut = StatutPresentation.Planifié;

    @Column(name = "fichiers")
    private String fichiers; // Liste des chemins séparés par des virgules

    // Enum pour le statut
    public enum StatutPresentation {
        Planifié, Annulé, Confirmé, Terminé
    }

    // Constructeurs
    public Presentation() {}

    public Presentation(Utilisateur utilisateur, LocalDate datePresentation, LocalDateTime heureDebut, LocalDateTime heureFin, String sujet, String description, StatutPresentation statut, String fichiers) {
        this.utilisateur = utilisateur;
        this.datePresentation = datePresentation;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
        this.fichiers = fichiers;
    }

    // Getters et Setters
    public Integer getIdPresentation() {
        return idPresentation;
    }

    public void setIdPresentation(Integer idPresentation) {
        this.idPresentation = idPresentation;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDate getDatePresentation() {
        return datePresentation;
    }

    public void setDatePresentation(LocalDate datePresentation) {
        this.datePresentation = datePresentation;
    }

    public LocalDateTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalDateTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalDateTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalDateTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutPresentation getStatut() {
        return statut;
    }

    public void setStatut(StatutPresentation statut) {
        this.statut = statut;
    }

    public String getFichiers() {
        return fichiers;
    }

    public void setFichiers(String fichiers) {
        this.fichiers = fichiers;
    }
}
