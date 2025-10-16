package com.example.departement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.departement.dto.PresentationDTO;
import com.example.departement.entity.Document;
import com.example.departement.entity.Notification;
import com.example.departement.entity.Presentation;
import com.example.departement.entity.Utilisateur;
import com.example.departement.entity.Vote;
import com.example.departement.repository.DocumentRepository;
import com.example.departement.repository.PresentationRepository;
import com.example.departement.repository.UtilisateurRepository;

@Service
@Transactional // CORRECTION : Ajouter @Transactional au niveau de la classe
public class PresentationService {

    private static final Logger logger = LoggerFactory.getLogger(PresentationService.class);

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
                                          String sujet, String description, 
                                          Presentation.StatutPresentation statut, 
                                          MultipartFile[] fichiers) throws IOException {
        
        logger.info("=== CRÉATION PRÉSENTATION ===");
        logger.info("User ID: {}, Sujet: {}, Date: {}, Statut: {}", idUtilisateur, sujet, datePresentation, statut);
        
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + idUtilisateur));

        Presentation presentation = new Presentation();
        presentation.setUtilisateur(utilisateur);
        presentation.setDatePresentation(datePresentation);
        presentation.setSujet(sujet);
        presentation.setDescription(description);
        presentation.setStatut(statut);

        // Sauvegarder d'abord la présentation
        presentation = presentationRepository.save(presentation);
        logger.info("Présentation sauvegardée avec ID: {}", presentation.getIdPresentation());

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
            logger.info("Fichiers sauvegardés: {}", savedFiles.size());
        }

        presentation = presentationRepository.save(presentation);

        // Créer une notification pour tous les utilisateurs
        String message = String.format("Nouvelle présentation ajoutée: '%s' par %s %s le %s",
            sujet, utilisateur.getPrenom(), utilisateur.getNom(), datePresentation.toString());
        notificationService.createNotificationForAll(message, Notification.TypeAction.AJOUT, presentation.getIdPresentation());

        logger.info("=== PRÉSENTATION CRÉÉE AVEC SUCCÈS ===");
        return presentation;
    }

    // CORRECTION : Utiliser une query sans JOIN FETCH pour éviter MultipleBagFetchException
    @Transactional(readOnly = true)
    public List<PresentationDTO> getAllPresentations() {
        logger.info("=== RÉCUPÉRATION TOUTES LES PRÉSENTATIONS ===");
        List<Presentation> presentations = presentationRepository.findAllWithDetails();
        logger.info("Nombre de présentations: {}", presentations.size());

        // Initialiser explicitement les collections une par une pour éviter MultipleBagFetchException
        presentations.forEach(p -> {
            // Charger les documents
            p.getDocuments().size();
            // Charger les votes
            p.getVotes().size();
            // Charger les commentaires
            p.getCommentaires().size();
        });

        // Convertir en DTOs
        return presentations.stream()
            .map(PresentationDTO::new)
            .collect(java.util.stream.Collectors.toList());
    }

    // CORRECTION : Ajouter readOnly pour les lectures
    @Transactional(readOnly = true)
    public Optional<PresentationDTO> getPresentationById(Integer id) {
        logger.info("Récupération présentation ID: {}", id);
        Optional<Presentation> presentation = presentationRepository.findByIdWithDetails(id);

        // Initialiser les collections une par une
        presentation.ifPresent(p -> {
            // Charger les documents
            p.getDocuments().size();
            // Charger les votes
            p.getVotes().size();
            // Charger les commentaires
            p.getCommentaires().size();
        });

        return presentation.map(PresentationDTO::new);
    }

    @Transactional(readOnly = true)
    public List<PresentationDTO> getPresentationsByUtilisateur(Integer idUtilisateur) {
        logger.info("Récupération présentations utilisateur ID: {}", idUtilisateur);
        List<Presentation> presentations = presentationRepository.findByUtilisateurIdUtilisateurWithDetails(idUtilisateur);

        // Initialiser les collections une par une
        presentations.forEach(p -> {
            // Charger les documents
            p.getDocuments().size();
            // Charger les votes
            p.getVotes().size();
            // Charger les commentaires
            p.getCommentaires().size();
        });

        return presentations.stream()
            .map(PresentationDTO::new)
            .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PresentationDTO> getPresentationsByStatut(Presentation.StatutPresentation statut) {
        logger.info("Récupération présentations statut: {}", statut);
        List<Presentation> presentations = presentationRepository.findByStatutWithDetails(statut);

        // Initialiser les collections une par une
        presentations.forEach(p -> {
            // Charger les documents
            p.getDocuments().size();
            // Charger les votes
            p.getVotes().size();
            // Charger les commentaires
            p.getCommentaires().size();
        });

        return presentations.stream()
            .map(PresentationDTO::new)
            .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Presentation> getPresentationsByPeriod(LocalDate startDate, LocalDate endDate) {
        logger.info("Récupération présentations période: {} - {}", startDate, endDate);
        return presentationRepository.findByDatePresentationBetweenWithDocuments(startDate, endDate);
    }

    // Mettre à jour une présentation
    public Presentation updatePresentation(Integer id, Integer idUtilisateur, 
                                          LocalDate datePresentation, String sujet, 
                                          String description, Presentation.StatutPresentation statut, 
                                          MultipartFile[] fichiers) throws IOException {
        
        logger.info("=== MISE À JOUR PRÉSENTATION ID: {} ===", id);
        
        Presentation presentation = presentationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Présentation non trouvée avec l'ID: " + id));

        // Vérifier que l'utilisateur est le propriétaire
        if (!presentation.getUtilisateur().getIdUtilisateur().equals(idUtilisateur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette présentation");
        }

        presentation.setDatePresentation(datePresentation);
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

        logger.info("=== PRÉSENTATION MISE À JOUR ===");
        return presentation;
    }

    // Supprimer une présentation
    public void deletePresentation(Integer id, Integer idUtilisateur) {
        logger.info("=== SUPPRESSION PRÉSENTATION ID: {} ===", id);
        
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
                    logger.error("Erreur suppression fichier: {}", file, e);
                }
            }
        }

        presentationRepository.deleteById(id);

        // Créer une notification de suppression
        String message = String.format("Présentation supprimée: '%s' par %s", sujet, nomComplet);
        notificationService.createNotificationForAll(message, Notification.TypeAction.SUPPRESSION, null);

        logger.info("=== PRÉSENTATION SUPPRIMÉE ===");
    }

    @Transactional(readOnly = true)
    public List<PresentationDTO> searchPresentations(String term) {
        if (term == null || term.trim().isEmpty()) {
            return getAllPresentations();
        }
        logger.info("Recherche: {}", term);
        List<Presentation> results = presentationRepository.searchByTermWithDetails(term.trim());

        // Initialiser les collections une par une
        results.forEach(p -> {
            // Charger les documents
            p.getDocuments().size();
            // Charger les votes
            p.getVotes().size();
            // Charger les commentaires
            p.getCommentaires().size();
        });

        return results.stream()
            .map(PresentationDTO::new)
            .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPresentationStats(Integer idPresentation) {
        Presentation presentation = presentationRepository.findById(idPresentation)
            .orElseThrow(() -> new RuntimeException("Présentation non trouvée"));

        // Initialiser les collections une par une
        // Charger les documents
        presentation.getDocuments().size();
        // Charger les votes
        presentation.getVotes().size();
        // Charger les commentaires
        presentation.getCommentaires().size();

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