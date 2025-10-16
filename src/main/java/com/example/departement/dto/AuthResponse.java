package com.example.departement.dto;

import java.time.LocalDateTime;

public class AuthResponse {
    private String token;
    private Integer idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String poste;
    private String matricule;
    private String departement;
    private String photoDeProfil;
    private LocalDateTime dateInscription;

    public AuthResponse(String token, Integer idUtilisateur, String nom, String prenom, String email, String poste, String matricule, String departement, String photoDeProfil, LocalDateTime dateInscription) {
        this.token = token;
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.poste = poste;
        this.matricule = matricule;
        this.departement = departement;
        this.photoDeProfil = photoDeProfil;
        this.dateInscription = dateInscription;
    }

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public String getPhotoDeProfil() { return photoDeProfil; }
    public void setPhotoDeProfil(String photoDeProfil) { this.photoDeProfil = photoDeProfil; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
}
