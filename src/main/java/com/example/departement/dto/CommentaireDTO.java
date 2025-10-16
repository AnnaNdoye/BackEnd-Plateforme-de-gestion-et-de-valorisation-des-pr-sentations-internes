package com.example.departement.dto;

import java.time.LocalDateTime;

import com.example.departement.entity.Commentaire;

public class CommentaireDTO {
    private Integer idCommentaire;
    private Integer idPresentation;
    private Integer idUtilisateur;
    private String nomUtilisateur;
    private String prenomUtilisateur;
    private String contenu;
    private LocalDateTime dateCommentaire;

    // Constructeur par défaut
    public CommentaireDTO() {}

    // Constructeur à partir d'une entité Commentaire
    public CommentaireDTO(Commentaire commentaire) {
        this.idCommentaire = commentaire.getIdCommentaire();
        this.idPresentation = commentaire.getPresentation().getIdPresentation();
        this.idUtilisateur = commentaire.getUtilisateur().getIdUtilisateur();
        this.nomUtilisateur = commentaire.getUtilisateur().getNom();
        this.prenomUtilisateur = commentaire.getUtilisateur().getPrenom();
        this.contenu = commentaire.getContenu();
        this.dateCommentaire = commentaire.getDateCommentaire();
    }

    // Getters et Setters
    public Integer getIdCommentaire() { return idCommentaire; }
    public void setIdCommentaire(Integer idCommentaire) { this.idCommentaire = idCommentaire; }

    public Integer getIdPresentation() { return idPresentation; }
    public void setIdPresentation(Integer idPresentation) { this.idPresentation = idPresentation; }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public String getPrenomUtilisateur() { return prenomUtilisateur; }
    public void setPrenomUtilisateur(String prenomUtilisateur) { this.prenomUtilisateur = prenomUtilisateur; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDateCommentaire() { return dateCommentaire; }
    public void setDateCommentaire(LocalDateTime dateCommentaire) { this.dateCommentaire = dateCommentaire; }
}
