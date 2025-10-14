package com.example.departement.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.departement.dto.AuthResponse;
import com.example.departement.dto.LoginRequest;
import com.example.departement.dto.RegisterRequest;
import com.example.departement.service.UtilisateurService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = utilisateurService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur interne du serveur\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = utilisateurService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur interne du serveur\"}");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            AuthResponse response = utilisateurService.getProfile(email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur interne du serveur\"}");
        }
    }

    @PutMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfile(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("email") String email,
            @RequestParam("poste") String poste,
            @RequestParam("matricule") String matricule,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            AuthResponse response = utilisateurService.updateProfile(userEmail, nom, prenom, email, poste, matricule, photo);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur interne du serveur\"}");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            // Pour JWT, la déconnexion est principalement côté client
            // Ici, on peut simplement retourner un succès
            // Si on voulait invalider le token côté serveur, on pourrait utiliser une blacklist
            return ResponseEntity.ok("{\"message\":\"Déconnexion réussie\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur lors de la déconnexion\"}");
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"message\":\"Email requis\"}");
            }
            utilisateurService.requestPasswordReset(email);
            return ResponseEntity.ok("{\"message\":\"Demande de réinitialisation envoyée\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur interne du serveur\"}");
        }
    }
}
