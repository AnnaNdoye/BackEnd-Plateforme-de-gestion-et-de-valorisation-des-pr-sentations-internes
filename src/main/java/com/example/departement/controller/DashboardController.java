package com.example.departement.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.departement.entity.Presentation;
import com.example.departement.repository.PresentationRepository;
import com.example.departement.repository.UtilisateurRepository;
import com.example.departement.util.JwtUtils;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtils jwtUtils;

    // Obtenir les statistiques du dashboard
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(@RequestHeader("Authorization") String token) {
        try {
            logger.info("=== DÉBUT RÉCUPÉRATION STATS DASHBOARD ===");
            
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            logger.info("Utilisateur ID: {}, Email: {}", currentUserId, email);

            Map<String, Object> stats = new HashMap<>();

            // Statistiques globales
            long totalPresentations = presentationRepository.count();
            long totalUsers = utilisateurRepository.count();
            
            logger.info("Total présentations: {}, Total utilisateurs: {}", totalPresentations, totalUsers);

            // Présentations par statut - CORRECTION
            long planifiees = 0;
            long confirmees = 0;
            long terminees = 0;
            long annulees = 0;
            
            try {
                List<Presentation> planifiesList = presentationRepository.findByStatut(Presentation.StatutPresentation.Planifié);
                planifiees = planifiesList.size();
                logger.info("Présentations Planifiées: {}", planifiees);
                
                List<Presentation> confirmeesList = presentationRepository.findByStatut(Presentation.StatutPresentation.Confirmé);
                confirmees = confirmeesList.size();
                logger.info("Présentations Confirmées: {}", confirmees);
                
                List<Presentation> termineesList = presentationRepository.findByStatut(Presentation.StatutPresentation.Terminé);
                terminees = termineesList.size();
                logger.info("Présentations Terminées: {}", terminees);
                
                List<Presentation> annuleesList = presentationRepository.findByStatut(Presentation.StatutPresentation.Annulé);
                annulees = annuleesList.size();
                logger.info("Présentations Annulées: {}", annulees);
                
            } catch (Exception e) {
                logger.error("Erreur lors du comptage par statut: {}", e.getMessage(), e);
            }

            // Présentations de l'utilisateur
            long myPresentationsCount = 0;
            try {
                List<Presentation> myPresentations = presentationRepository.findByUtilisateurIdUtilisateur(currentUserId);
                myPresentationsCount = myPresentations.size();
                logger.info("Présentations de l'utilisateur {}: {}", currentUserId, myPresentationsCount);
            } catch (Exception e) {
                logger.error("Erreur lors du comptage des présentations utilisateur: {}", e.getMessage(), e);
            }

            // Présentations à venir (30 prochains jours)
            List<Presentation> upcomingPresentations = null;
            try {
                LocalDate today = LocalDate.now();
                LocalDate futureDate = today.plusMonths(1);
                upcomingPresentations = presentationRepository.findByDatePresentationBetween(today, futureDate);
                logger.info("Présentations à venir dans les 30 prochains jours: {}", upcomingPresentations.size());
                
                // Log détaillé des présentations à venir
                for (Presentation p : upcomingPresentations) {
                    logger.debug("Présentation à venir: ID={}, Sujet={}, Date={}, Statut={}", 
                               p.getIdPresentation(), p.getSujet(), p.getDatePresentation(), p.getStatut());
                }
            } catch (Exception e) {
                logger.error("Erreur lors de la récupération des présentations à venir: {}", e.getMessage(), e);
                upcomingPresentations = List.of();
            }

            // Construction de la réponse
            stats.put("totalPresentations", totalPresentations);
            stats.put("totalUsers", totalUsers);
            stats.put("planifiees", planifiees);
            stats.put("confirmees", confirmees);
            stats.put("terminees", terminees);
            stats.put("annulees", annulees);
            stats.put("myPresentations", myPresentationsCount);
            stats.put("upcomingCount", upcomingPresentations.size());
            stats.put("upcomingPresentations", upcomingPresentations);

            logger.info("=== STATS ENVOYÉES ===");
            logger.info("Total: {}, Planifiées: {}, Confirmées: {}, Terminées: {}, Annulées: {}", 
                       totalPresentations, planifiees, confirmees, terminees, annulees);
            logger.info("=== FIN RÉCUPÉRATION STATS DASHBOARD ===");
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("=== ERREUR DASHBOARD ===", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors du chargement des statistiques: " + e.getMessage()));
        }
    }
}