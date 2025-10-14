package com.example.departement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.departement.entity.Vote;
import com.example.departement.service.VoteService;
import com.example.departement.util.JwtUtils;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "*")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.example.departement.repository.UtilisateurRepository utilisateurRepository;

    // Ajouter ou mettre à jour un vote
    @PostMapping
    public ResponseEntity<?> addOrUpdateVote(
            @RequestParam("idPresentation") Integer idPresentation,
            @RequestParam("note") Integer note,
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            Vote vote = voteService.addOrUpdateVote(idPresentation, currentUserId, note);
            return ResponseEntity.ok(vote);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir tous les votes d'une présentation
    @GetMapping("/presentation/{idPresentation}")
    public ResponseEntity<List<Vote>> getVotesByPresentation(@PathVariable Integer idPresentation) {
        try {
            List<Vote> votes = voteService.getVotesByPresentation(idPresentation);
            return ResponseEntity.ok(votes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtenir le vote d'un utilisateur pour une présentation
    @GetMapping("/presentation/{idPresentation}/my")
    public ResponseEntity<?> getMyVote(
            @PathVariable Integer idPresentation,
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            return voteService.getVoteByUserAndPresentation(idPresentation, currentUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Supprimer un vote
    @DeleteMapping("/{idVote}")
    public ResponseEntity<?> deleteVote(
            @PathVariable Integer idVote,
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getUsernameFromToken(jwtToken);
            Integer currentUserId = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                .getIdUtilisateur();

            voteService.deleteVote(idVote, currentUserId);
            return ResponseEntity.ok(Map.of("message", "Vote supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir la moyenne des votes
    @GetMapping("/presentation/{idPresentation}/average")
    public ResponseEntity<?> getAverageNote(@PathVariable Integer idPresentation) {
        try {
            Double average = voteService.getAverageNote(idPresentation);
            return ResponseEntity.ok(Map.of("average", average != null ? average : 0.0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir le nombre de votes
    @GetMapping("/presentation/{idPresentation}/count")
    public ResponseEntity<?> getVoteCount(@PathVariable Integer idPresentation) {
        try {
            Long count = voteService.getVoteCount(idPresentation);
            return ResponseEntity.ok(Map.of("count", count != null ? count : 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}