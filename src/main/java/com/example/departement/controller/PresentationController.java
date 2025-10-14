package com.example.departement.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('USER')")
public class PresentationController {

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
            @RequestParam("datePresentation") String datePresentationStr,
            @RequestParam("heureDebut") String heureDebutStr,
            @RequestParam("heureFin") String heureFinStr,
            @RequestParam("sujet") String sujet,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("statut") Presentation.StatutPresentation statut,
            @RequestParam(value = "fichiers", required = false) MultipartFile[] fichiers,
            @RequestHeader("Authorization") String token) {

        try {
            // Extraire l'email utilisateur du token
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email).get().getIdUtilisateur();

            // Vérifier que l'utilisateur crée pour lui-même
            if (!currentUserId.equals(idUtilisateur)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");
            }

            LocalDate datePresentation = LocalDate.parse(datePresentationStr);
            LocalDateTime heureDebut = LocalDateTime.parse(heureDebutStr);
            LocalDateTime heureFin = LocalDateTime.parse(heureFinStr);

            Presentation presentation = presentationService.createPresentation(idUtilisateur, datePresentation, heureDebut, heureFin, sujet, description, statut, fichiers);
            return ResponseEntity.ok(presentation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la création: " + e.getMessage());
        }
    }

    // Obtenir toutes les présentations
    @GetMapping("/all")
    public ResponseEntity<List<Presentation>> getAllPresentations() {
        List<Presentation> presentations = presentationService.getAllPresentations();
        return ResponseEntity.ok(presentations);
    }

    // Obtenir une présentation par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPresentationById(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email).get().getIdUtilisateur();

            Optional<Presentation> presentation = presentationService.getPresentationById(id);
            if (presentation.isPresent()) {
                // Vérifier que l'utilisateur est le propriétaire
                if (!presentation.get().getUtilisateur().getIdUtilisateur().equals(currentUserId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");
                }
                return ResponseEntity.ok(presentation.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur: " + e.getMessage());
        }
    }

    // Obtenir les présentations de l'utilisateur connecté
    @GetMapping("/my")
    public ResponseEntity<?> getMyPresentations(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email).get().getIdUtilisateur();

            List<Presentation> presentations = presentationService.getPresentationsByUtilisateur(currentUserId);
            return ResponseEntity.ok(presentations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur: " + e.getMessage());
        }
    }

    // Mettre à jour une présentation
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePresentation(
            @PathVariable Integer id,
            @RequestParam("idUtilisateur") Integer idUtilisateur,
            @RequestParam("datePresentation") String datePresentationStr,
            @RequestParam("heureDebut") String heureDebutStr,
            @RequestParam("heureFin") String heureFinStr,
            @RequestParam("sujet") String sujet,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("statut") Presentation.StatutPresentation statut,
            @RequestParam(value = "fichiers", required = false) MultipartFile[] fichiers,
            @RequestHeader("Authorization") String token) {

        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email).get().getIdUtilisateur();

            if (!currentUserId.equals(idUtilisateur)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");
            }

            LocalDate datePresentation = LocalDate.parse(datePresentationStr);
            LocalDateTime heureDebut = LocalDateTime.parse(heureDebutStr);
            LocalDateTime heureFin = LocalDateTime.parse(heureFinStr);

            Presentation presentation = presentationService.updatePresentation(id, idUtilisateur, datePresentation, heureDebut, heureFin, sujet, description, statut, fichiers);
            return ResponseEntity.ok(presentation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    // Supprimer une présentation
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePresentation(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email).get().getIdUtilisateur();

            presentationService.deletePresentation(id, currentUserId);
            return ResponseEntity.ok("Présentation supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    // Rechercher des présentations
    @GetMapping("/search")
    public ResponseEntity<List<Presentation>> searchPresentations(@RequestParam("term") String term) {
        List<Presentation> presentations = presentationService.searchPresentations(term);
        return ResponseEntity.ok(presentations);
    }
}
