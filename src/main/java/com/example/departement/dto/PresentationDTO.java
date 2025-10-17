package com.example.departement.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.departement.entity.Presentation;

public class PresentationDTO {
    private Integer idPresentation;
    private Integer idUtilisateur;
    private String nomUtilisateur;
    private String prenomUtilisateur;
    private String emailUtilisateur;
    private String departementUtilisateur;
    private LocalDate datePresentation;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String sujet;
    private String description;
    private String statut;
    private String fichier;
    private List<DocumentDTO> documents;
    private List<VoteDTO> votes;
    private List<CommentaireDTO> commentaires;

    // Constructeur par défaut
    public PresentationDTO() {}

    // Constructeur à partir d'une entité Presentation
    public PresentationDTO(Presentation presentation) {
        this.idPresentation = presentation.getIdPresentation();
        this.idUtilisateur = presentation.getUtilisateur().getIdUtilisateur();
        this.nomUtilisateur = presentation.getUtilisateur().getNom();
        this.prenomUtilisateur = presentation.getUtilisateur().getPrenom();
        this.emailUtilisateur = presentation.getUtilisateur().getEmail();
        this.departementUtilisateur = presentation.getUtilisateur().getDepartement();
        this.datePresentation = presentation.getDatePresentation();
        this.heureDebut = presentation.getHeureDebut();
        this.heureFin = presentation.getHeureFin();
        this.sujet = presentation.getSujet();
        this.description = presentation.getDescription();
        this.statut = presentation.getStatut().toString();
        this.fichier = presentation.getFichier();

        // Convertir les collections en DTOs
        this.documents = presentation.getDocuments().stream()
            .map(DocumentDTO::new)
            .collect(Collectors.toList());

        this.votes = presentation.getVotes().stream()
            .map(VoteDTO::new)
            .collect(Collectors.toList());

        this.commentaires = presentation.getCommentaires().stream()
            .map(CommentaireDTO::new)
            .collect(Collectors.toList());
    }

    // Getters et Setters
    public Integer getIdPresentation() { return idPresentation; }
    public void setIdPresentation(Integer idPresentation) { this.idPresentation = idPresentation; }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public String getPrenomUtilisateur() { return prenomUtilisateur; }
    public void setPrenomUtilisateur(String prenomUtilisateur) { this.prenomUtilisateur = prenomUtilisateur; }

    public String getEmailUtilisateur() { return emailUtilisateur; }
    public void setEmailUtilisateur(String emailUtilisateur) { this.emailUtilisateur = emailUtilisateur; }

    public String getDepartementUtilisateur() { return departementUtilisateur; }
    public void setDepartementUtilisateur(String departementUtilisateur) { this.departementUtilisateur = departementUtilisateur; }

    public LocalDate getDatePresentation() { return datePresentation; }
    public void setDatePresentation(LocalDate datePresentation) { this.datePresentation = datePresentation; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getFichier() { return fichier; }
    public void setFichier(String fichier) { this.fichier = fichier; }

    public List<DocumentDTO> getDocuments() { return documents; }
    public void setDocuments(List<DocumentDTO> documents) { this.documents = documents; }

    public List<VoteDTO> getVotes() { return votes; }
    public void setVotes(List<VoteDTO> votes) { this.votes = votes; }

    public List<CommentaireDTO> getCommentaires() { return commentaires; }
    public void setCommentaires(List<CommentaireDTO> commentaires) { this.commentaires = commentaires; }
}
