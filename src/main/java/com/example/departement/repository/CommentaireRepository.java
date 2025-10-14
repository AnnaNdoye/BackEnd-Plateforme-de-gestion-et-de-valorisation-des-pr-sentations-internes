package com.example.departement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.departement.entity.Commentaire;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Integer> {
    List<Commentaire> findByPresentationIdPresentationOrderByDateCommentaireDesc(Integer idPresentation);
    List<Commentaire> findByUtilisateurIdUtilisateur(Integer idUtilisateur);
}