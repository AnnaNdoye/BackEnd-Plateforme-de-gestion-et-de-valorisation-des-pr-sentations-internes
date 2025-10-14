package com.example.departement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Integer idNotification;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Le message est obligatoire")
    private String message;

    @Column(name = "date_de_reception")
    private LocalDateTime dateDeReception;

    @Column(name = "type_action")
    @Enumerated(EnumType.STRING)
    private TypeAction typeAction;

    @Column(name = "id_presentation")
    private Integer idPresentation;

    public enum TypeAction {
        AJOUT, MODIFICATION, SUPPRESSION
    }

    @PrePersist
    protected void onCreate() {
        dateDeReception = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getIdNotification() { return idNotification; }
    public void setIdNotification(Integer idNotification) { this.idNotification = idNotification; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getDateDeReception() { return dateDeReception; }
    public void setDateDeReception(LocalDateTime dateDeReception) { this.dateDeReception = dateDeReception; }
    
    public TypeAction getTypeAction() { return typeAction; }
    public void setTypeAction(TypeAction typeAction) { this.typeAction = typeAction; }
    
    public Integer getIdPresentation() { return idPresentation; }
    public void setIdPresentation(Integer idPresentation) { this.idPresentation = idPresentation; }
}
