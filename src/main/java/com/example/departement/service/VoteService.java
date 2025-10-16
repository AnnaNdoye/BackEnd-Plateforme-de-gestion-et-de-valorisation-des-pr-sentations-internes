package com.example.departement.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.departement.dto.VoteDTO;
import com.example.departement.entity.Presentation;
import com.example.departement.entity.Utilisateur;
import com.example.departement.entity.Vote;
import com.example.departement.repository.PresentationRepository;
import com.example.departement.repository.UtilisateurRepository;
import com.example.departement.repository.VoteRepository;

@Service
@Transactional
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Ajouter ou mettre à jour un vote
    public VoteDTO addOrUpdateVote(Integer idPresentation, Integer idUtilisateur, Integer note) {
        if (note < 1 || note > 5) {
            throw new RuntimeException("La note doit être entre 1 et 5");
        }

        Presentation presentation = presentationRepository.findById(idPresentation)
            .orElseThrow(() -> new RuntimeException("Présentation non trouvée"));

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur a déjà voté
        Optional<Vote> existingVote = voteRepository
            .findByPresentationIdPresentationAndUtilisateurIdUtilisateur(idPresentation, idUtilisateur);

        Vote vote;
        if (existingVote.isPresent()) {
            // Mettre à jour le vote existant
            vote = existingVote.get();
            vote.setNote(note);
        } else {
            // Créer un nouveau vote
            vote = new Vote();
            vote.setPresentation(presentation);
            vote.setUtilisateur(utilisateur);
            vote.setNote(note);
        }

        Vote savedVote = voteRepository.save(vote);
        return new VoteDTO(savedVote);
    }

    // Obtenir tous les votes d'une présentation
    public List<VoteDTO> getVotesByPresentation(Integer idPresentation) {
        List<Vote> votes = voteRepository.findByPresentationIdPresentation(idPresentation);
        return votes.stream()
            .map(VoteDTO::new)
            .collect(Collectors.toList());
    }

    // Obtenir le vote d'un utilisateur pour une présentation
    public Optional<Vote> getVoteByUserAndPresentation(Integer idPresentation, Integer idUtilisateur) {
        return voteRepository.findByPresentationIdPresentationAndUtilisateurIdUtilisateur(
            idPresentation, idUtilisateur);
    }

    // Supprimer un vote
    public void deleteVote(Integer idVote, Integer idUtilisateur) {
        Vote vote = voteRepository.findById(idVote)
            .orElseThrow(() -> new RuntimeException("Vote non trouvé"));

        if (!vote.getUtilisateur().getIdUtilisateur().equals(idUtilisateur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce vote");
        }

        voteRepository.deleteById(idVote);
    }

    // Obtenir la moyenne des votes
    public Double getAverageNote(Integer idPresentation) {
        return voteRepository.getAverageNoteByPresentation(idPresentation);
    }

    // Obtenir le nombre de votes
    public Long getVoteCount(Integer idPresentation) {
        return voteRepository.countVotesByPresentation(idPresentation);
    }
}