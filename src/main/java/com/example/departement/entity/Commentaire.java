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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "commentaires")
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commentaire")
    private Integer idCommentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presentation", nullable = false)
    @JsonIgnoreProperties({"votes", "commentaires", "documents"})
    private Presentation presentation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    @JsonIgnoreProperties({"votes", "commentaires", "presentations", "motDePasse"})
    private Utilisateur utilisateur;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    private String contenu;

    @Column(name = "date_commentaire")
    private LocalDateTime dateCommentaire;

    @PrePersist
    protected void onCreate() {
        dateCommentaire = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateCommentaire = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getIdCommentaire() { return idCommentaire; }
    public void setIdCommentaire(Integer idCommentaire) { this.idCommentaire = idCommentaire; }
    
    public Presentation getPresentation() { return presentation; }
    public void setPresentation(Presentation presentation) { this.presentation = presentation; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    
    public LocalDateTime getDateCommentaire() { return dateCommentaire; }
    public void setDateCommentaire(LocalDateTime dateCommentaire) { this.dateCommentaire = dateCommentaire; }
}