package com.example.departement.controller;

import com.example.departement.entity.Presentation;
import com.example.departement.repository.PresentationRepository;
import com.example.departement.repository.UtilisateurRepository;
import com.example.departement.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

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
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            Map<String, Object> stats = new HashMap<>();

            // Statistiques globales
            long totalPresentations = presentationRepository.count();
            long totalUsers = utilisateurRepository.count();

            // Présentations par statut
            long planifiees = presentationRepository.findByStatut(Presentation.StatutPresentation.Planifié).size();
            long confirmees = presentationRepository.findByStatut(Presentation.StatutPresentation.Confirmé).size();
            long terminees = presentationRepository.findByStatut(Presentation.StatutPresentation.Terminé).size();
            long annulees = presentationRepository.findByStatut(Presentation.StatutPresentation.Annulé).size();

            // Présentations de l'utilisateur
            List<Presentation> myPresentations = presentationRepository.findByUtilisateurIdUtilisateur(currentUserId);
            long myPresentationsCount = myPresentations.size();

            // Présentations à venir
            LocalDate today = LocalDate.now();
            List<Presentation> upcomingPresentations = presentationRepository
                .findByDatePresentationBetween(today, today.plusMonths(1));

            stats.put("totalPresentations", totalPresentations);
            stats.put("totalUsers", totalUsers);
            stats.put("planifiees", planifiees);
            stats.put("confirmees", confirmees);
            stats.put("terminees", terminees);
            stats.put("annulees", annulees);
            stats.put("myPresentations", myPresentationsCount);
            stats.put("upcomingCount", upcomingPresentations.size());
            stats.put("upcomingPresentations", upcomingPresentations);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}