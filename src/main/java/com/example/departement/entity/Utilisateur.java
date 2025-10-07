package com.example.departement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

    @Column(name = "nom", nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Column(name = "prenom", nullable = false)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String prenom;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Column(name = "photo_de_profil")
    private String photoDeProfil;

    @Column(name = "poste")
    private String poste;

    @Column(name = "matricule", nullable = false, unique = true)
    @NotBlank(message = "Le matricule est obligatoire")
    @Size(min = 1, max = 100, message = "Le matricule doit contenir entre 1 et 100 caractères")
    private String matricule;

    @Column(name = "mot_de_passe", nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    @Column(name = "date_inscription", nullable = false, updatable = false)
    private LocalDateTime dateInscription;

    // Constructeurs
    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String photoDeProfil, String poste, String matricule, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.photoDeProfil = photoDeProfil;
        this.poste = poste;
        this.matricule = matricule;
        this.motDePasse = motDePasse;
        this.dateInscription = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoDeProfil() { return photoDeProfil; }
    public void setPhotoDeProfil(String photoDeProfil) { this.photoDeProfil = photoDeProfil; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
}
