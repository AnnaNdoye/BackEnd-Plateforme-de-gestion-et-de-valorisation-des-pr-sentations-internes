package com.example.departement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "departements")
public class Departement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departement")
    private Integer idDepartement;
    
    @Column(name = "nom_departement", nullable = false, unique = true)
    @NotBlank(message = "Le nom du département ne peut pas être vide")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nomDepartement;
    
    @Column(nullable = false, unique = true, length = 10)
    @NotBlank(message = "Le code du département ne peut pas être vide")
    @Size(min = 2, max = 10, message = "Le code doit contenir entre 2 et 10 caractères")
    private String code;
    
    @Column(length = 500)
    private String description;
    
    // Constructeurs
    public Departement() {}
    
    public Departement(String nomDepartement, String code, String description) {
        this.nomDepartement = nomDepartement;
        this.code = code;
        this.description = description;
    }
    
    // Getters et Setters
    public Integer getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(Integer idDepartement) {
        this.idDepartement = idDepartement;
    }
    
    public String getNomDepartement() {
        return nomDepartement;
    }
    
    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}