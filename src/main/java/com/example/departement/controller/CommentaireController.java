package com.example.departement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.departement.entity.Commentaire;
import com.example.departement.service.CommentaireService;
import com.example.departement.util.JwtUtils;

@RestController
@RequestMapping("/api/commentaires")
public class CommentaireController {

    @Autowired
    private CommentaireService commentaireService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.example.departement.repository.UtilisateurRepository utilisateurRepository;

    // Ajouter un commentaire
    @PostMapping
    public ResponseEntity<?> addCommentaire(
            @RequestParam("idPresentation") Integer idPresentation,
            @RequestParam("contenu") String contenu,
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            Commentaire commentaire = commentaireService.addCommentaire(idPresentation, currentUserId, contenu);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentaire);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir tous les commentaires d'une présentation
    @GetMapping("/presentation/{idPresentation}")
    public ResponseEntity<List<Commentaire>> getCommentairesByPresentation(@PathVariable Integer idPresentation) {
        try {
            List<Commentaire> commentaires = commentaireService.getCommentairesByPresentation(idPresentation);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Mettre à jour un commentaire
    @PutMapping("/{idCommentaire}")
    public ResponseEntity<?> updateCommentaire(
            @PathVariable Integer idCommentaire,
            @RequestParam("contenu") String contenu,
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            Commentaire commentaire = commentaireService.updateCommentaire(idCommentaire, currentUserId, contenu);
            return ResponseEntity.ok(commentaire);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Supprimer un commentaire
    @DeleteMapping("/{idCommentaire}")
    public ResponseEntity<?> deleteCommentaire(
            @PathVariable Integer idCommentaire,
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            commentaireService.deleteCommentaire(idCommentaire, currentUserId);
            return ResponseEntity.ok(Map.of("message", "Commentaire supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les commentaires d'un utilisateur
    @GetMapping("/my")
    public ResponseEntity<?> getMyCommentaires(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            List<Commentaire> commentaires = commentaireService.getCommentairesByUtilisateur(currentUserId);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}