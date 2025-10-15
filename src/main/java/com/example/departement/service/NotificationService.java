package com.example.departement.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationEmployeRepository notificationEmployeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Créer une notification pour tous les utilisateurs
    public void createNotificationForAll(String message, Notification.TypeAction typeAction, Integer idPresentation) {
        try {
            logger.info("Création d'une notification pour tous les utilisateurs: {}", message);
            
            // Créer la notification
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setTypeAction(typeAction);
            notification.setIdPresentation(idPresentation);
            notification = notificationRepository.save(notification);
            
            logger.info("Notification créée avec ID: {}", notification.getIdNotification());

            // Associer à tous les utilisateurs
            List<Utilisateur> allUsers = utilisateurRepository.findAll();
            logger.info("Nombre d'utilisateurs trouvés: {}", allUsers.size());
            
            for (Utilisateur user : allUsers) {
                try {
                    NotificationEmploye ne = new NotificationEmploye();
                    ne.setIdUtilisateur(user.getIdUtilisateur());
                    ne.setIdNotification(notification.getIdNotification());
                    ne.setLue(false);
                    notificationEmployeRepository.save(ne);
                    logger.debug("Notification associée à l'utilisateur ID: {}", user.getIdUtilisateur());
                } catch (Exception e) {
                    logger.error("Erreur lors de l'association de la notification à l'utilisateur {}: {}", 
                               user.getIdUtilisateur(), e.getMessage());
                }
            }
            
            logger.info("Notification créée et distribuée avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
    }

    // Obtenir toutes les notifications d'un utilisateur
    public List<NotificationEmploye> getNotificationsByUser(Integer idUtilisateur) {
        try {
            logger.info("Récupération des notifications pour l'utilisateur ID: {}", idUtilisateur);
            List<NotificationEmploye> notifications = notificationEmployeRepository.findByIdUtilisateurOrderByDateDesc(idUtilisateur);
            logger.info("Nombre de notifications trouvées: {}", notifications.size());
            return notifications;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des notifications: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération des notifications", e);
        }
    }

    // Obtenir les notifications non lues d'un utilisateur
    public List<NotificationEmploye> getUnreadNotificationsByUser(Integer idUtilisateur) {
        try {
            logger.info("Récupération des notifications non lues pour l'utilisateur ID: {}", idUtilisateur);
            List<NotificationEmploye> notifications = notificationEmployeRepository.findUnreadByIdUtilisateur(idUtilisateur);
            logger.info("Nombre de notifications non lues: {}", notifications.size());
            return notifications;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des notifications non lues: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération des notifications non lues", e);
        }
    }

    // Marquer une notification comme lue
    public void markAsRead(Integer idUtilisateur, Integer idNotification) {
        try {
            logger.info("Marquage de la notification {} comme lue pour l'utilisateur {}", idNotification, idUtilisateur);
            NotificationEmployeId id = new NotificationEmployeId(idUtilisateur, idNotification);
            NotificationEmploye ne = notificationEmployeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
            ne.setLue(true);
            notificationEmployeRepository.save(ne);
            logger.info("Notification marquée comme lue avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors du marquage de la notification comme lue: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors du marquage de la notification", e);
        }
    }

    // Marquer toutes les notifications comme lues
    public void markAllAsRead(Integer idUtilisateur) {
        try {
            logger.info("Marquage de toutes les notifications comme lues pour l'utilisateur {}", idUtilisateur);
            List<NotificationEmploye> notifications = notificationEmployeRepository.findByIdUtilisateur(idUtilisateur);
            logger.info("Nombre de notifications à marquer: {}", notifications.size());
            
            for (NotificationEmploye ne : notifications) {
                ne.setLue(true);
            }
            notificationEmployeRepository.saveAll(notifications);
            logger.info("Toutes les notifications marquées comme lues avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors du marquage de toutes les notifications: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors du marquage des notifications", e);
        }
    }

    // Supprimer une notification pour un utilisateur
    public void deleteNotificationForUser(Integer idUtilisateur, Integer idNotification) {
        try {
            logger.info("Suppression de la notification {} pour l'utilisateur {}", idNotification, idUtilisateur);
            NotificationEmployeId id = new NotificationEmployeId(idUtilisateur, idNotification);
            notificationEmployeRepository.deleteById(id);
            logger.info("Notification supprimée avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la notification: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression de la notification", e);
        }
    }

    // Obtenir le nombre de notifications non lues
    public Long getUnreadCount(Integer idUtilisateur) {
        try {
            Long count = (long) getUnreadNotificationsByUser(idUtilisateur).size();
            logger.debug("Nombre de notifications non lues pour l'utilisateur {}: {}", idUtilisateur, count);
            return count;
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des notifications non lues: {}", e.getMessage(), e);
            return 0L;
        }
    }
}