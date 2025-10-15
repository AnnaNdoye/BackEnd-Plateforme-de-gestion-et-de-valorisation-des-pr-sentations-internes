package com.example.departement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private PresentationService presentationService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.example.departement.repository.UtilisateurRepository utilisateurRepository;

    // Créer une présentation
    @PostMapping("/create")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> createPresentation(
            @RequestParam("idUtilisateur") Integer idUtilisateur,
            @RequestParam("datePresentation") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePresentation,
            @RequestParam("sujet") String sujet,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("statut") Presentation.StatutPresentation statut,
            @RequestParam(value = "fichiers", required = false) MultipartFile[] fichiers,
            @RequestHeader("Authorization") String token) {

        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            if (!currentUserId.equals(idUtilisateur)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");
            }

            Presentation presentation = presentationService.createPresentation(
                idUtilisateur, datePresentation, sujet, description, statut, fichiers);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(presentation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir toutes les présentations
    @GetMapping("/all")
    public ResponseEntity<List<Presentation>> getAllPresentations() {
        try {
            List<Presentation> presentations = presentationService.getAllPresentations();
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtenir une présentation par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPresentationById(@PathVariable Integer id) {
        try {
            return presentationService.getPresentationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
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

            List<Presentation> presentations = presentationService.getPresentationsByUtilisateur(currentUserId);
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les présentations par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Presentation>> getPresentationsByStatut(
            @PathVariable Presentation.StatutPresentation statut) {
        try {
            List<Presentation> presentations = presentationService.getPresentationsByStatut(statut);
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtenir les présentations par période
    @GetMapping("/period")
    public ResponseEntity<List<Presentation>> getPresentationsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Presentation> presentations = presentationService.getPresentationsByPeriod(startDate, endDate);
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Mettre à jour une présentation
    @PutMapping("/{id}")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> updatePresentation(
            @PathVariable Integer id,
            @RequestParam("idUtilisateur") Integer idUtilisateur,
            @RequestParam("datePresentation") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePresentation,
            @RequestParam("sujet") String sujet,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("statut") Presentation.StatutPresentation statut,
            @RequestParam(value = "fichiers", required = false) MultipartFile[] fichiers,
            @RequestHeader("Authorization") String token) {

        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            if (!currentUserId.equals(idUtilisateur)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");
            }

            Presentation presentation = presentationService.updatePresentation(
                id, idUtilisateur, datePresentation, sujet, description, statut, fichiers);
            
            return ResponseEntity.ok(presentation);
        } catch (Exception e) {
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
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            presentationService.deletePresentation(id, currentUserId);
            return ResponseEntity.ok(Map.of("message", "Présentation supprimée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Rechercher des présentations
    @GetMapping("/search")
    public ResponseEntity<List<Presentation>> searchPresentations(@RequestParam("term") String term) {
        try {
            List<Presentation> presentations = presentationService.searchPresentations(term);
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtenir les statistiques d'une présentation
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getPresentationStats(@PathVariable Integer id) {
        try {
            Map<String, Object> stats = presentationService.getPresentationStats(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}