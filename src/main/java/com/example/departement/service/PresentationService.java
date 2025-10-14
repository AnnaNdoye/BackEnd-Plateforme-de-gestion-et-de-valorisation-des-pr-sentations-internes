package com.example.departement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.departement.entity.Presentation;
import com.example.departement.entity.Utilisateur;
import com.example.departement.repository.PresentationRepository;
import com.example.departement.repository.UtilisateurRepository;

@Service
public class PresentationService {

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private final Path rootLocation = Paths.get("uploads/presentations");

    // Créer une présentation
    public Presentation createPresentation(Integer idUtilisateur, LocalDate datePresentation, LocalDateTime heureDebut, LocalDateTime heureFin, String sujet, String description, Presentation.StatutPresentation statut, MultipartFile[] fichiers) throws IOException {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(idUtilisateur);
        if (utilisateur.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        String fichiersPaths = null;
        if (fichiers != null && fichiers.length > 0) {
            fichiersPaths = saveFiles(fichiers);
        }

        Presentation presentation = new Presentation(utilisateur.get(), datePresentation, heureDebut, heureFin, sujet, description, statut, fichiersPaths);
        return presentationRepository.save(presentation);
    }

    // Obtenir toutes les présentations
    public List<Presentation> getAllPresentations() {
        return presentationRepository.findAll();
    }

    // Obtenir une présentation par ID
    public Optional<Presentation> getPresentationById(Integer id) {
        return presentationRepository.findById(id);
    }

    // Obtenir les présentations d'un utilisateur
    public List<Presentation> getPresentationsByUtilisateur(Integer idUtilisateur) {
        return presentationRepository.findByUtilisateurIdUtilisateur(idUtilisateur);
    }

    // Mettre à jour une présentation
    public Presentation updatePresentation(Integer id, Integer idUtilisateur, LocalDate datePresentation, LocalDateTime heureDebut, LocalDateTime heureFin, String sujet, String description, Presentation.StatutPresentation statut, MultipartFile[] fichiers) throws IOException {
        Optional<Presentation> existingPresentation = presentationRepository.findById(id);
        if (existingPresentation.isEmpty()) {
            throw new RuntimeException("Présentation non trouvée");
        }

        Presentation presentation = existingPresentation.get();
        // Vérifier que l'utilisateur est le propriétaire
        if (!presentation.getUtilisateur().getIdUtilisateur().equals(idUtilisateur)) {
            throw new RuntimeException("Accès non autorisé");
        }

        String fichiersPaths = presentation.getFichiers();
        if (fichiers != null && fichiers.length > 0) {
            fichiersPaths = saveFiles(fichiers);
        }

        presentation.setDatePresentation(datePresentation);
        presentation.setHeureDebut(heureDebut);
        presentation.setHeureFin(heureFin);
        presentation.setSujet(sujet);
        presentation.setDescription(description);
        presentation.setStatut(statut);
        presentation.setFichiers(fichiersPaths);

        return presentationRepository.save(presentation);
    }

    // Supprimer une présentation
    public void deletePresentation(Integer id, Integer idUtilisateur) {
        Optional<Presentation> presentation = presentationRepository.findById(id);
        if (presentation.isEmpty()) {
            throw new RuntimeException("Présentation non trouvée");
        }

        // Vérifier que l'utilisateur est le propriétaire
        if (!presentation.get().getUtilisateur().getIdUtilisateur().equals(idUtilisateur)) {
            throw new RuntimeException("Accès non autorisé");
        }

        presentationRepository.deleteById(id);
    }

    // Rechercher des présentations par terme
    public List<Presentation> searchPresentations(String term) {
        return presentationRepository.searchByTerm(term);
    }

    // Sauvegarder les fichiers
    private String saveFiles(MultipartFile[] fichiers) throws IOException {
        Files.createDirectories(rootLocation);
        StringBuilder paths = new StringBuilder();
        for (MultipartFile file : fichiers) {
            if (!file.isEmpty()) {
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = rootLocation.resolve(filename);
                Files.copy(file.getInputStream(), filePath);
                if (paths.length() > 0) {
                    paths.append(",");
                }
                paths.append(filename);
            }
        }
        return paths.toString();
    }
}
