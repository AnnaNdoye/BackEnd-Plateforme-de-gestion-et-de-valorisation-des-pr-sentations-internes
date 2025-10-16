package com.example.departement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.departement.entity.Commentaire;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Integer> {
    @Query("SELECT c FROM Commentaire c JOIN FETCH c.presentation p JOIN FETCH p.utilisateur WHERE c.presentation.idPresentation = :idPresentation ORDER BY c.dateCommentaire DESC")
    List<Commentaire> findByPresentationIdPresentationOrderByDateCommentaireDesc(@Param("idPresentation") Integer idPresentation);
    List<Commentaire> findByUtilisateurIdUtilisateur(Integer idUtilisateur);
}
