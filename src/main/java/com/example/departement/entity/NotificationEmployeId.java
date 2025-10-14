package com.example.departement.entity;

import java.io.Serializable;
import java.util.Objects;

public class NotificationEmployeId implements Serializable {
    private Integer idUtilisateur;
    private Integer idNotification;

    public NotificationEmployeId() {}

    public NotificationEmployeId(Integer idUtilisateur, Integer idNotification) {
        this.idUtilisateur = idUtilisateur;
        this.idNotification = idNotification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationEmployeId that = (NotificationEmployeId) o;
        return Objects.equals(idUtilisateur, that.idUtilisateur) &&
               Objects.equals(idNotification, that.idNotification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUtilisateur, idNotification);
    }

    // Getters et Setters
    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    
    public Integer getIdNotification() { return idNotification; }
    public void setIdNotification(Integer idNotification) { this.idNotification = idNotification; }
}