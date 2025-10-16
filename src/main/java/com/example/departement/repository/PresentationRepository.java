package com.example.departement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.departement.entity.Presentation;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, Integer> {

    // CORRECTION : Queries sans JOIN FETCH pour les collections multiples (pour éviter MultipleBagFetchException)
    // Les collections seront chargées paresseusement et initialisées manuellement dans le service

    @Query("SELECT DISTINCT p FROM Presentation p " +
           "LEFT JOIN FETCH p.utilisateur " +
           "ORDER BY p.datePresentation DESC")
    List<Presentation> findAllWithDetails();

    @Query("SELECT DISTINCT p FROM Presentation p " +
           "LEFT JOIN FETCH p.utilisateur " +
           "WHERE p.idPresentation = :id")
    Optional<Presentation> findByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT DISTINCT p FROM Presentation p " +
           "LEFT JOIN FETCH p.utilisateur " +
           "WHERE p.utilisateur.idUtilisateur = :idUtilisateur " +
           "ORDER BY p.datePresentation DESC")
    List<Presentation> findByUtilisateurIdUtilisateurWithDetails(@Param("idUtilisateur") Integer idUtilisateur);

    @Query("SELECT DISTINCT p FROM Presentation p " +
           "LEFT JOIN FETCH p.utilisateur " +
           "WHERE p.statut = :statut " +
           "ORDER BY p.datePresentation DESC")
    List<Presentation> findByStatutWithDetails(@Param("statut") Presentation.StatutPresentation statut);

    @Query("SELECT DISTINCT p FROM Presentation p " +
           "LEFT JOIN FETCH p.documents " +
           "LEFT JOIN FETCH p.votes " +
           "LEFT JOIN FETCH p.utilisateur " +
           "WHERE p.datePresentation BETWEEN :startDate AND :endDate " +
           "ORDER BY p.datePresentation ASC")
    List<Presentation> findByDatePresentationBetweenWithDocuments(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT DISTINCT p FROM Presentation p " +
           "LEFT JOIN FETCH p.documents " +
           "LEFT JOIN FETCH p.votes " +
           "LEFT JOIN FETCH p.commentaires " +
           "LEFT JOIN FETCH p.utilisateur " +
           "WHERE LOWER(p.sujet) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Presentation> searchByTermWithDetails(@Param("term") String term);

    // Méthodes simples sans JOIN FETCH (pour les cas où on n'a pas besoin des collections)

    List<Presentation> findByUtilisateurIdUtilisateur(Integer idUtilisateur);

    List<Presentation> findByStatut(Presentation.StatutPresentation statut);

    List<Presentation> findByDatePresentationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM Presentation p WHERE LOWER(p.sujet) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Presentation> searchByTerm(@Param("term") String term);

    List<Presentation> findByUtilisateurIdUtilisateurAndStatut(Integer idUtilisateur, Presentation.StatutPresentation statut);

    List<Presentation> findAllByOrderByDatePresentationDesc();
}
