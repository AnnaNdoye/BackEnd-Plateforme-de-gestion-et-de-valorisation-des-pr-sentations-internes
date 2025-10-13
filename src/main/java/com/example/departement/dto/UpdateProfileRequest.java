package com.example.departement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    private String poste;

    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    private String photoDeProfil;

    // Getters and setters
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

    public String getPhotoDeProfil() { return photoDeProfil; }
    public void setPhotoDeProfil(String photoDeProfil) { this.photoDeProfil = photoDeProfil; }
}
