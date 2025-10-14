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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_presentation", "id_utilisateur"})
})
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vote")
    private Integer idVote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presentation", nullable = false)
    @JsonIgnoreProperties({"votes", "commentaires", "documents"})
    private Presentation presentation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    @JsonIgnoreProperties({"votes", "commentaires", "presentations", "motDePasse"})
    private Utilisateur utilisateur;

    @Column(name = "note", nullable = false)
    @Min(value = 1, message = "La note minimum est 1")
    @Max(value = 5, message = "La note maximum est 5")
    private Integer note;

    @Column(name = "date_vote")
    private LocalDateTime dateVote;

    @PrePersist
    protected void onCreate() {
        dateVote = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateVote = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getIdVote() { return idVote; }
    public void setIdVote(Integer idVote) { this.idVote = idVote; }
    
    public Presentation getPresentation() { return presentation; }
    public void setPresentation(Presentation presentation) { this.presentation = presentation; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }
    
    public LocalDateTime getDateVote() { return dateVote; }
    public void setDateVote(LocalDateTime dateVote) { this.dateVote = dateVote; }
}