package com.example.departement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "presentations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Presentation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presentation")
    private Integer idPresentation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    @JsonIgnoreProperties({"presentations", "votes", "commentaires", "motDePasse"})
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
    @Size(min = 2, max = 255)
    private String sujet;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    @NotNull(message = "Le statut est obligatoire")
    private StatutPresentation statut = StatutPresentation.Planifié;

    @Column(name = "fichier", columnDefinition = "TEXT")
    private String fichier;

    @OneToMany(mappedBy = "presentation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("presentation")
    private List<Document> documents;

    @OneToMany(mappedBy = "presentation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("presentation")
    private List<Vote> votes;

    @OneToMany(mappedBy = "presentation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("presentation")
    private List<Commentaire> commentaires;

    public enum StatutPresentation {
        Planifié, Annulé, Confirmé, Terminé
    }

    // Constructeurs
    public Presentation() {}

    // Getters et Setters complets
    public Integer getIdPresentation() { return idPresentation; }
    public void setIdPresentation(Integer idPresentation) { this.idPresentation = idPresentation; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public LocalDate getDatePresentation() { return datePresentation; }
    public void setDatePresentation(LocalDate datePresentation) { this.datePresentation = datePresentation; }
    
    public LocalDateTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalDateTime heureDebut) { this.heureDebut = heureDebut; }
    
    public LocalDateTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalDateTime heureFin) { this.heureFin = heureFin; }
    
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public StatutPresentation getStatut() { return statut; }
    public void setStatut(StatutPresentation statut) { this.statut = statut; }
    
    public String getFichier() { return fichier; }
    public void setFichier(String fichier) { this.fichier = fichier; }
    
    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
    
    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }
    
    public List<Commentaire> getCommentaires() { return commentaires; }
    public void setCommentaires(List<Commentaire> commentaires) { this.commentaires = commentaires; }
}