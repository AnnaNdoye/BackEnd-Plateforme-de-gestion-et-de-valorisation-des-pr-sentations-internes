package com.example.departement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.departement.entity.NotificationEmploye;
import com.example.departement.entity.NotificationEmployeId;

@Repository
public interface NotificationEmployeRepository extends JpaRepository<NotificationEmploye, NotificationEmployeId> {
    List<NotificationEmploye> findByIdUtilisateur(Integer idUtilisateur);
    
    @Query("SELECT ne FROM NotificationEmploye ne " +
           "LEFT JOIN FETCH ne.utilisateur u " +
           "LEFT JOIN FETCH ne.notification n " +
           "WHERE ne.idUtilisateur = :idUtilisateur " +
           "ORDER BY ne.notification.dateDeReception DESC")
    List<NotificationEmploye> findByIdUtilisateurOrderByDateDesc(@Param("idUtilisateur") Integer idUtilisateur);

    @Query("SELECT ne FROM NotificationEmploye ne " +
           "LEFT JOIN FETCH ne.utilisateur u " +
           "LEFT JOIN FETCH ne.notification n " +
           "WHERE ne.idUtilisateur = :idUtilisateur AND ne.lue = false")
    List<NotificationEmploye> findUnreadByIdUtilisateur(@Param("idUtilisateur") Integer idUtilisateur);
}