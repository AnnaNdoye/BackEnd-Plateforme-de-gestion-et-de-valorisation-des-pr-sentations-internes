package com.example.departement.service;

import com.example.departement.entity.*;
import com.example.departement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CommentaireService {

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Ajouter un commentaire
    public Commentaire addCommentaire(Integer idPresentation, Integer idUtilisateur, String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new RuntimeException("Le contenu du commentaire ne peut pas être vide");
        }

        Presentation presentation = presentationRepository.findById(idPresentation)
            .orElseThrow(() -> new RuntimeException("Présentation non trouvée"));

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Commentaire commentaire = new Commentaire();
        commentaire.setPresentation(presentation);
        commentaire.setUtilisateur(utilisateur);
        commentaire.setContenu(contenu.trim());

        return commentaireRepository.save(commentaire);
    }

    // Obtenir tous les commentaires d'une présentation
    public List<Commentaire> getCommentairesByPresentation(Integer idPresentation) {
        return commentaireRepository.findByPresentationIdPresentationOrderByDateCommentaireDesc(idPresentation);
    }

    // Mettre à jour un commentaire
    public Commentaire updateCommentaire(Integer idCommentaire, Integer idUtilisateur, String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new RuntimeException("Le contenu du commentaire ne peut pas être vide");
        }

        Commentaire commentaire = commentaireRepository.findById(idCommentaire)
            .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        if (!commentaire.getUtilisateur().getIdUtilisateur().equals(idUtilisateur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce commentaire");
        }

        commentaire.setContenu(contenu.trim());
        return commentaireRepository.save(commentaire);
    }

    // Supprimer un commentaire
    public void deleteCommentaire(Integer idCommentaire, Integer idUtilisateur) {
        Commentaire commentaire = commentaireRepository.findById(idCommentaire)
            .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        if (!commentaire.getUtilisateur().getIdUtilisateur().equals(idUtilisateur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce commentaire");
        }

        commentaireRepository.deleteById(idCommentaire);
    }

    // Obtenir les commentaires d'un utilisateur
    public List<Commentaire> getCommentairesByUtilisateur(Integer idUtilisateur) {
        return commentaireRepository.findByUtilisateurIdUtilisateur(idUtilisateur);
    }
}