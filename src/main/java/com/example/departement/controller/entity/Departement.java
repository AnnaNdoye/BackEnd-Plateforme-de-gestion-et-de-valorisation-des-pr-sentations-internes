package com.example.departement.controller.entity;

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
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom du département ne peut pas être vide")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;
    
    @Column(nullable = false, unique = true, length = 10)
    @NotBlank(message = "Le code du département ne peut pas être vide")
    @Size(min = 2, max = 10, message = "Le code doit contenir entre 2 et 10 caractères")
    private String code;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "nombre_employes")
    private Integer nombreEmployes = 0;
    
    // Constructeurs
    public Departement() {}
    
    public Departement(String nom, String code, String description, Integer nombreEmployes) 
    {
        this.nom = nom;
        this.code = code;
        this.description = description;
        this.nombreEmployes = nombreEmployes;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } 
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getNombreEmployes() { return nombreEmployes; }
    public void setNombreEmployes(Integer nombreEmployes) { this.nombreEmployes = nombreEmployes; }
}