package com.example.departement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.departement.dto.AuthResponse;
import com.example.departement.dto.LoginRequest;
import com.example.departement.dto.RegisterRequest;
import com.example.departement.entity.Utilisateur;
import com.example.departement.repository.UtilisateurRepository;
import com.example.departement.util.JwtUtils;

@Service
public class UtilisateurService {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        if (utilisateurRepository.existsByMatricule(request.getMatricule())) {
            throw new RuntimeException("Un utilisateur avec ce matricule existe déjà");
        }

        Utilisateur utilisateur = new Utilisateur(
                request.getNom(),
                request.getPrenom(),
                request.getEmail(),
                null, // photoDeProfil
                request.getPoste(),
                request.getMatricule(),
                passwordEncoder.encode(request.getMotDePasse())
        );

        utilisateur = utilisateurRepository.save(utilisateur);

        String token = jwtUtils.generateToken(utilisateur.getEmail());

        return new AuthResponse(
                token,
                utilisateur.getIdUtilisateur(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getPoste(),
                utilisateur.getMatricule()
        );
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Tentative de connexion pour l'email: {}", request.getEmail());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Utilisateur non trouvé pour l'email: {}", request.getEmail());
                    return new RuntimeException("Email ou mot de passe incorrect");
                });

        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            logger.warn("Mot de passe incorrect pour l'email: {}", request.getEmail());
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        String token = jwtUtils.generateToken(utilisateur.getEmail());
        logger.info("Connexion réussie pour l'email: {}", request.getEmail());

        return new AuthResponse(
                token,
                utilisateur.getIdUtilisateur(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getPoste(),
                utilisateur.getMatricule()
        );
    }
}
