package com.example.departement.dto;

import java.time.LocalDateTime;

import com.example.departement.entity.Vote;

public class VoteDTO {
    private Integer idVote;
    private Integer idPresentation;
    private Integer idUtilisateur;
    private String nomUtilisateur;
    private String prenomUtilisateur;
    private Integer note;
    private LocalDateTime dateVote;

    // Constructeur par défaut
    public VoteDTO() {}

    // Constructeur à partir d'une entité Vote
    public VoteDTO(Vote vote) {
        this.idVote = vote.getIdVote();
        this.idPresentation = vote.getPresentation().getIdPresentation();
        this.idUtilisateur = vote.getUtilisateur().getIdUtilisateur();
        this.nomUtilisateur = vote.getUtilisateur().getNom();
        this.prenomUtilisateur = vote.getUtilisateur().getPrenom();
        this.note = vote.getNote();
        this.dateVote = vote.getDateVote();
    }

    // Getters et Setters
    public Integer getIdVote() { return idVote; }
    public void setIdVote(Integer idVote) { this.idVote = idVote; }

    public Integer getIdPresentation() { return idPresentation; }
    public void setIdPresentation(Integer idPresentation) { this.idPresentation = idPresentation; }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public String getPrenomUtilisateur() { return prenomUtilisateur; }
    public void setPrenomUtilisateur(String prenomUtilisateur) { this.prenomUtilisateur = prenomUtilisateur; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public LocalDateTime getDateVote() { return dateVote; }
    public void setDateVote(LocalDateTime dateVote) { this.dateVote = dateVote; }
}
