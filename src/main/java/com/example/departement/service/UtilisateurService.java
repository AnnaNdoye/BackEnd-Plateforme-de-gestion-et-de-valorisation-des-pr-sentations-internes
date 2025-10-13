
package com.example.departement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.departement.dto.AuthResponse;
import com.example.departement.dto.LoginRequest;
import com.example.departement.dto.RegisterRequest;
import com.example.departement.dto.UpdateProfileRequest;
import com.example.departement.entity.Utilisateur;
import com.example.departement.repository.UtilisateurRepository;
import com.example.departement.util.JwtUtils;

@Service
public class UtilisateurService {

    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);

    @Value("${app.upload.dir}")
    private String uploadDir;

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
                utilisateur.getMatricule(),
                utilisateur.getPhotoDeProfil(),
                utilisateur.getDateInscription()
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
                utilisateur.getMatricule(),
                utilisateur.getPhotoDeProfil(),
                utilisateur.getDateInscription()
        );
    }

    public AuthResponse getProfile(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return new AuthResponse(
                null, // No token for profile fetch
                utilisateur.getIdUtilisateur(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getPoste(),
                utilisateur.getMatricule(),
                utilisateur.getPhotoDeProfil(),
                utilisateur.getDateInscription()
        );
    }

    public AuthResponse updateProfile(String email, String nom, String prenom, String userEmail, String poste, String matricule, MultipartFile photo) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'email est changé et s'il est déjà pris
        if (!utilisateur.getEmail().equals(userEmail) && utilisateurRepository.existsByEmail(userEmail)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Vérifier si le matricule est changé et s'il est déjà pris
        if (!utilisateur.getMatricule().equals(matricule) && utilisateurRepository.existsByMatricule(matricule)) {
            throw new RuntimeException("Un utilisateur avec ce matricule existe déjà");
        }

        // Gérer l'upload de la photo
        String photoPath = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                // Créer le répertoire s'il n'existe pas
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Générer un nom de fichier unique
                String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Stocker le chemin relatif
                photoPath = "/uploads/" + fileName;
            } catch (IOException e) {
                logger.error("Erreur lors de l'upload de la photo: {}", e.getMessage());
                throw new RuntimeException("Erreur lors de l'upload de la photo");
            }
        }

        // Mettre à jour les champs
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(userEmail);
        utilisateur.setPoste(poste);
        utilisateur.setMatricule(matricule);
        if (photoPath != null) {
            utilisateur.setPhotoDeProfil(photoPath);
        }

        utilisateur = utilisateurRepository.save(utilisateur);

        return new AuthResponse(
                null, // No token for profile update
                utilisateur.getIdUtilisateur(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getPoste(),
                utilisateur.getMatricule(),
                utilisateur.getPhotoDeProfil(),
                utilisateur.getDateInscription()
        );
    }
}
