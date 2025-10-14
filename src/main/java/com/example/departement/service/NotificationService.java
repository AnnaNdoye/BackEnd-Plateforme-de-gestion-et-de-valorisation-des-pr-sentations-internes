package com.example.departement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.departement.entity.Notification;
import com.example.departement.entity.NotificationEmploye;
import com.example.departement.entity.NotificationEmployeId;
import com.example.departement.entity.Utilisateur;
import com.example.departement.repository.NotificationEmployeRepository;
import com.example.departement.repository.NotificationRepository;
import com.example.departement.repository.UtilisateurRepository;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationEmployeRepository notificationEmployeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Créer une notification pour tous les utilisateurs
    public void createNotificationForAll(String message, Notification.TypeAction typeAction, Integer idPresentation) {
        // Créer la notification
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTypeAction(typeAction);
        notification.setIdPresentation(idPresentation);
        notification = notificationRepository.save(notification);

        // Associer à tous les utilisateurs
        List<Utilisateur> allUsers = utilisateurRepository.findAll();
        for (Utilisateur user : allUsers) {
            NotificationEmploye ne = new NotificationEmploye();
            ne.setIdUtilisateur(user.getIdUtilisateur());
            ne.setIdNotification(notification.getIdNotification());
            ne.setLue(false);
            notificationEmployeRepository.save(ne);
        }
    }

    // Obtenir toutes les notifications d'un utilisateur
    public List<NotificationEmploye> getNotificationsByUser(Integer idUtilisateur) {
        return notificationEmployeRepository.findByIdUtilisateurOrderByDateDesc(idUtilisateur);
    }

    // Obtenir les notifications non lues d'un utilisateur
    public List<NotificationEmploye> getUnreadNotificationsByUser(Integer idUtilisateur) {
        return notificationEmployeRepository.findUnreadByIdUtilisateur(idUtilisateur);
    }

    // Marquer une notification comme lue
    public void markAsRead(Integer idUtilisateur, Integer idNotification) {
        NotificationEmployeId id = new NotificationEmployeId(idUtilisateur, idNotification);
        NotificationEmploye ne = notificationEmployeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        ne.setLue(true);
        notificationEmployeRepository.save(ne);
    }

    // Marquer toutes les notifications comme lues
    public void markAllAsRead(Integer idUtilisateur) {
        List<NotificationEmploye> notifications = notificationEmployeRepository.findByIdUtilisateur(idUtilisateur);
        for (NotificationEmploye ne : notifications) {
            ne.setLue(true);
        }
        notificationEmployeRepository.saveAll(notifications);
    }

    // Supprimer une notification pour un utilisateur
    public void deleteNotificationForUser(Integer idUtilisateur, Integer idNotification) {
        NotificationEmployeId id = new NotificationEmployeId(idUtilisateur, idNotification);
        notificationEmployeRepository.deleteById(id);
    }

    // Obtenir le nombre de notifications non lues
    public Long getUnreadCount(Integer idUtilisateur) {
        return (long) getUnreadNotificationsByUser(idUtilisateur).size();
    }
}