package com.example.departement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_employe")
@IdClass(NotificationEmployeId.class)
public class NotificationEmploye {
    @Id
    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

    @Id
    @Column(name = "id_notification")
    private Integer idNotification;

    @Column(name = "lue")
    private Boolean lue = false;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", insertable = false, updatable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_notification", insertable = false, updatable = false)
    private Notification notification;

    // Getters et Setters
    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    
    public Integer getIdNotification() { return idNotification; }
    public void setIdNotification(Integer idNotification) { this.idNotification = idNotification; }
    
    public Boolean getLue() { return lue; }
    public void setLue(Boolean lue) { this.lue = lue; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public Notification getNotification() { return notification; }
    public void setNotification(Notification notification) { this.notification = notification; }
}