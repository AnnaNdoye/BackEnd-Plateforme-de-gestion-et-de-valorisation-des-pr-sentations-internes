package com.example.departement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.departement.entity.Document;
import com.example.departement.entity.Notification;
import com.example.departement.entity.Presentation;
import com.example.departement.entity.Utilisateur;
import com.example.departement.entity.Vote;
import com.example.departement.repository.DocumentRepository;
import com.example.departement.repository.PresentationRepository;
import com.example.departement.repository.UtilisateurRepository;

@Service
@Transactional
public class PresentationService {

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Créer une présentation
    public Presentation createPresentation(Integer idUtilisateur, LocalDate datePresentation, 
                                          LocalDateTime heureDebut, LocalDateTime heureFin, 
                                          String sujet, String description, 
                                          Presentation.StatutPresentation statut, 
                                          MultipartFile[] fichiers) throws IOException {
        
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + idUtilisateur));

        // Validation des heures
        if (heureDebut.isAfter(heureFin)) {
            throw new RuntimeException("L'heure de début doit être avant l'heure de fin");
        }

        Presentation presentation = new Presentation();
        presentation.setUtilisateur(utilisateur);
        presentation.setDatePresentation(datePresentation);
        presentation.setHeureDebut(heureDebut);
        presentation.setHeureFin(heureFin);
        presentation.setSujet(sujet);
        presentation.setDescription(description);
        presentation.setStatut(statut);

        // Sauvegarder d'abord la présentation
        presentation = presentationRepository.save(presentation);

        // Gérer les fichiers
        if (fichiers != null && fichiers.length > 0) {
            List<String> savedFiles = saveFiles(fichiers, presentation.getIdPresentation());
            presentation.setFichier(String.join(",", savedFiles));
            
            // Créer les entrées de documents
            for (String fileName : savedFiles) {
                Document document = new Document();
                document.setPresentation(presentation);
                document.setNom(fileName);
                document.setChemin("uploads/presentations/" + fileName);
                documentRepository.save(document);
            }
        }

        presentation = presentationRepository.save(presentation);

        // Créer une notification pour tous les utilisateurs
        String message = String.format("Nouvelle présentation ajoutée: '%s' par %s %s le %s",
            sujet, utilisateur.getPrenom(), utilisateur.getNom(), datePresentation.toString());
        notificationService.createNotificationForAll(message, Notification.TypeAction.AJOUT, presentation.getIdPresentation());

        return presentation;
    }

    // Obtenir toutes les présentations
    public List<Presentation> getAllPresentations() {
        return presentationRepository.findAllByOrderByDatePresentationDesc();
    }

    // Obtenir une présentation par ID
    public Optional<Presentation> getPresentationById(Integer id) {
        return presentationRepository.findById(id);
    }

    // Obtenir les présentations d'un utilisateur
    public List<Presentation> getPresentationsByUtilisateur(Integer idUtilisateur) {
        return presentationRepository.findByUtilisateurIdUtilisateur(idUtilisateur);
    }

    // Obtenir les présentations par statut
    public List<Presentation> getPresentationsByStatut(Presentation.StatutPresentation statut) {
        return presentationRepository.findByStatut(statut);
    }

    // Obtenir les présentations par période
    public List<Presentation> getPresentationsByPeriod(LocalDate startDate, LocalDate endDate) {
        return presentationRepository.findByDatePresentationBetween(startDate, endDate);
    }

    // Mettre à jour une présentation
    public Presentation updatePresentation(Integer id, Integer idUtilisateur, 
                                          LocalDate datePresentation, LocalDateTime heureDebut, 
                                          LocalDateTime heureFin, String sujet, 
                                          String description, Presentation.StatutPresentation statut, 
                                          MultipartFile[] fichiers) throws IOException {
        
        Presentation presentation = presentationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Présentation non trouvée avec l'ID: " + id));

        // Vérifier que l'utilisateur est le propriétaire
        if (!presentation.getUtilisateur().getIdUtilisateur().equals(idUtilisateur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette présentation");
        }

        // Validation des heures
        if (heureDebut.isAfter(heureFin)) {
            throw new RuntimeException("L'heure de début doit être avant l'heure de fin");
        }

        presentation.setDatePresentation(datePresentation);
        presentation.setHeureDebut(heureDebut);
        presentation.setHeureFin(heureFin);
        presentation.setSujet(sujet);
        presentation.setDescription(description);
        presentation.setStatut(statut);

        // Gérer les nouveaux fichiers
        if (fichiers != null && fichiers.length > 0) {
            List<String> savedFiles = saveFiles(fichiers, presentation.getIdPresentation());
            
            // Ajouter aux fichiers existants
            String existingFiles = presentation.getFichier();
            List<String> allFiles = new ArrayList<>();
            if (existingFiles != null && !existingFiles.isEmpty()) {
                allFiles.addAll(Arrays.asList(existingFiles.split(",")));
            }
            allFiles.addAll(savedFiles);
            presentation.setFichier(String.join(",", allFiles));

            // Créer les entrées de documents pour les nouveaux fichiers
            for (String fileName : savedFiles) {
                Document document = new Document();
                document.setPresentation(presentation);
                document.setNom(fileName);
                document.setChemin("uploads/presentations/" + fileName);
                documentRepository.save(document);
            }
        }

        presentation = presentationRepository.save(presentation);

        // Créer une notification de modification
        String message = String.format("Présentation modifiée: '%s' par %s %s",
            sujet, presentation.getUtilisateur().getPrenom(), presentation.getUtilisateur().getNom());
        notificationService.createNotificationForAll(message, Notification.TypeAction.MODIFICATION, presentation.getIdPresentation());

        return presentation;
    }

    // Supprimer une présentation
    public void deletePresentation(Integer id, Integer idUtilisateur) {
        Presentation presentation = presentationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Présentation non trouvée avec l'ID: " + id));

        // Vérifier que l'utilisateur est le propriétaire
        if (!presentation.getUtilisateur().getIdUtilisateur().equals(idUtilisateur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cette présentation");
        }

        String sujet = presentation.getSujet();
        String nomComplet = presentation.getUtilisateur().getPrenom() + " " + presentation.getUtilisateur().getNom();

        // Supprimer les fichiers physiques
        if (presentation.getFichier() != null && !presentation.getFichier().isEmpty()) {
            String[] files = presentation.getFichier().split(",");
            for (String file : files) {
                try {
                    Path filePath = Paths.get(uploadDir, "presentations", file.trim());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    // Log l'erreur mais continue
                    System.err.println("Erreur lors de la suppression du fichier: " + file);
                }
            }
        }

        presentationRepository.deleteById(id);

        // Créer une notification de suppression
        String message = String.format("Présentation supprimée: '%s' par %s", sujet, nomComplet);
        notificationService.createNotificationForAll(message, Notification.TypeAction.SUPPRESSION, null);
    }

    // Rechercher des présentations
    public List<Presentation> searchPresentations(String term) {
        if (term == null || term.trim().isEmpty()) {
            return getAllPresentations();
        }
        return presentationRepository.searchByTerm(term.trim());
    }

    // Obtenir les statistiques d'une présentation
    public Map<String, Object> getPresentationStats(Integer idPresentation) {
        Presentation presentation = presentationRepository.findById(idPresentation)
            .orElseThrow(() -> new RuntimeException("Présentation non trouvée"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("nombreVotes", presentation.getVotes().size());
        stats.put("nombreCommentaires", presentation.getCommentaires().size());
        
        // Calculer la moyenne des notes
        if (!presentation.getVotes().isEmpty()) {
            double moyenne = presentation.getVotes().stream()
                .mapToInt(Vote::getNote)
                .average()
                .orElse(0.0);
            stats.put("moyenneVotes", Math.round(moyenne * 10.0) / 10.0);
        } else {
            stats.put("moyenneVotes", 0.0);
        }

        return stats;
    }

    // Sauvegarder les fichiers
    private List<String> saveFiles(MultipartFile[] fichiers, Integer idPresentation) throws IOException {
        Path uploadPath = Paths.get(uploadDir, "presentations");
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        List<String> savedFileNames = new ArrayList<>();

        for (MultipartFile file : fichiers) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String fileName = idPresentation + "_" + System.currentTimeMillis() + extension;
                Path filePath = uploadPath.resolve(fileName);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                savedFileNames.add(fileName);
            }
        }

        return savedFileNames;
    }
}