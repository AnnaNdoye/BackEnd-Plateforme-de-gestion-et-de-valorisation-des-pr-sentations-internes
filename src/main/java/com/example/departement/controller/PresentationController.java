package com.example.departement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.departement.entity.Presentation;
import com.example.departement.service.PresentationService;
import com.example.departement.util.JwtUtils;

@RestController
@RequestMapping("/api/presentations")
@CrossOrigin(origins = "*")
public class PresentationController {

    private static final Logger logger = LoggerFactory.getLogger(PresentationController.class);

    @Autowired
    private PresentationService presentationService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.example.departement.repository.UtilisateurRepository utilisateurRepository;

    // Créer une présentation
    @PostMapping("/create")
    public ResponseEntity<?> createPresentation(
            @RequestParam("idUtilisateur") Integer idUtilisateur,
            @RequestParam("datePresentation") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePresentation,
            @RequestParam("sujet") String sujet,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("statut") String statutStr,
            @RequestParam(value = "fichiers", required = false) MultipartFile[] fichiers,
            @RequestHeader("Authorization") String token) {

        try {
            logger.info("=== DÉBUT CRÉATION PRÉSENTATION ===");
            logger.info("ID Utilisateur: {}", idUtilisateur);
            logger.info("Date: {}", datePresentation);
            logger.info("Sujet: {}", sujet);
            logger.info("Description: {}", description);
            logger.info("Statut (String): {}", statutStr);
            logger.info("Nombre de fichiers: {}", fichiers != null ? fichiers.length : 0);

            // Validation du token
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            logger.info("Token validé - User ID du token: {}", currentUserId);

            if (!currentUserId.equals(idUtilisateur)) {
                logger.error("Accès non autorisé: {} vs {}", currentUserId, idUtilisateur);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès non autorisé"));
            }

            // Conversion du statut
            Presentation.StatutPresentation statut;
            try {
                statut = Presentation.StatutPresentation.valueOf(statutStr);
                logger.info("Statut converti: {}", statut);
            } catch (IllegalArgumentException e) {
                logger.error("Statut invalide: {}", statutStr);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Statut invalide: " + statutStr));
            }

            // Création de la présentation
            Presentation presentation = presentationService.createPresentation(
                idUtilisateur, datePresentation, sujet, description, statut, fichiers);
            
            logger.info("Présentation créée avec succès - ID: {}", presentation.getIdPresentation());
            logger.info("=== FIN CRÉATION PRÉSENTATION ===");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(presentation);
            
        } catch (Exception e) {
            logger.error("=== ERREUR CRÉATION PRÉSENTATION ===", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage(), "details", e.toString()));
        }
    }

    // Obtenir toutes les présentations
    @GetMapping("/all")
    public ResponseEntity<?> getAllPresentations() {
        try {
            logger.info("Récupération de toutes les présentations");
            List<Presentation> presentations = presentationService.getAllPresentations();
            logger.info("Nombre de présentations trouvées: {}", presentations.size());
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des présentations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir une présentation par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPresentationById(@PathVariable Integer id) {
        try {
            logger.info("Récupération de la présentation ID: {}", id);
            return presentationService.getPresentationById(id)
                .map(p -> {
                    logger.info("Présentation trouvée: {}", p.getSujet());
                    return ResponseEntity.ok(p);
                })
                .orElseGet(() -> {
                    logger.warn("Présentation non trouvée: ID {}", id);
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la présentation {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les présentations de l'utilisateur connecté
    @GetMapping("/my")
    public ResponseEntity<?> getMyPresentations(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            logger.info("Récupération des présentations de l'utilisateur ID: {}", currentUserId);
            List<Presentation> presentations = presentationService.getPresentationsByUtilisateur(currentUserId);
            logger.info("Nombre de présentations trouvées: {}", presentations.size());
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des présentations utilisateur", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les présentations par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<?> getPresentationsByStatut(@PathVariable String statut) {
        try {
            logger.info("Récupération des présentations avec statut: {}", statut);
            Presentation.StatutPresentation statutEnum = Presentation.StatutPresentation.valueOf(statut);
            List<Presentation> presentations = presentationService.getPresentationsByStatut(statutEnum);
            logger.info("Nombre de présentations trouvées: {}", presentations.size());
            return ResponseEntity.ok(presentations);
        } catch (IllegalArgumentException e) {
            logger.error("Statut invalide: {}", statut);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Statut invalide: " + statut));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération par statut", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les présentations par période
    @GetMapping("/period")
    public ResponseEntity<?> getPresentationsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            logger.info("Récupération des présentations entre {} et {}", startDate, endDate);
            List<Presentation> presentations = presentationService.getPresentationsByPeriod(startDate, endDate);
            logger.info("Nombre de présentations trouvées: {}", presentations.size());
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération par période", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Mettre à jour une présentation
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePresentation(
            @PathVariable Integer id,
            @RequestParam("idUtilisateur") Integer idUtilisateur,
            @RequestParam("datePresentation") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePresentation,
            @RequestParam("sujet") String sujet,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("statut") String statutStr,
            @RequestParam(value = "fichiers", required = false) MultipartFile[] fichiers,
            @RequestHeader("Authorization") String token) {

        try {
            logger.info("=== DÉBUT MISE À JOUR PRÉSENTATION ID: {} ===", id);
            
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            if (!currentUserId.equals(idUtilisateur)) {
                logger.error("Accès non autorisé pour mise à jour");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès non autorisé"));
            }

            Presentation.StatutPresentation statut = Presentation.StatutPresentation.valueOf(statutStr);

            Presentation presentation = presentationService.updatePresentation(
                id, idUtilisateur, datePresentation, sujet, description, statut, fichiers);
            
            logger.info("Présentation mise à jour avec succès");
            logger.info("=== FIN MISE À JOUR PRÉSENTATION ===");
            
            return ResponseEntity.ok(presentation);
        } catch (Exception e) {
            logger.error("=== ERREUR MISE À JOUR PRÉSENTATION ===", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Supprimer une présentation
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePresentation(
            @PathVariable Integer id, 
            @RequestHeader("Authorization") String token) {
        try {
            logger.info("=== DÉBUT SUPPRESSION PRÉSENTATION ID: {} ===", id);
            
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            presentationService.deletePresentation(id, currentUserId);
            logger.info("Présentation supprimée avec succès");
            logger.info("=== FIN SUPPRESSION PRÉSENTATION ===");
            
            return ResponseEntity.ok(Map.of("message", "Présentation supprimée avec succès"));
        } catch (Exception e) {
            logger.error("=== ERREUR SUPPRESSION PRÉSENTATION ===", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Rechercher des présentations
    @GetMapping("/search")
    public ResponseEntity<?> searchPresentations(@RequestParam("term") String term) {
        try {
            logger.info("Recherche de présentations avec le terme: {}", term);
            List<Presentation> presentations = presentationService.searchPresentations(term);
            logger.info("Nombre de résultats: {}", presentations.size());
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les statistiques d'une présentation
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getPresentationStats(@PathVariable Integer id) {
        try {
            logger.info("Récupération des stats pour la présentation ID: {}", id);
            Map<String, Object> stats = presentationService.getPresentationStats(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des stats", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}