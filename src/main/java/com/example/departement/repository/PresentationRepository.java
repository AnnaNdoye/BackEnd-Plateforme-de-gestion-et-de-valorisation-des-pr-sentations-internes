package com.example.departement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.departement.entity.Presentation;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, Integer> {

    // Trouver toutes les présentations d'un utilisateur
    List<Presentation> findByUtilisateurIdUtilisateur(Integer idUtilisateur);

    // Trouver les présentations par statut
    List<Presentation> findByStatut(Presentation.StatutPresentation statut);

    // Trouver les présentations entre deux dates
    List<Presentation> findByDatePresentationBetween(LocalDate startDate, LocalDate endDate);

    // Trouver les présentations entre deux dates avec documents et votes (pour éviter LazyInitializationException)
    @Query("SELECT p FROM Presentation p LEFT JOIN FETCH p.documents LEFT JOIN FETCH p.votes WHERE p.datePresentation BETWEEN :startDate AND :endDate")
    List<Presentation> findByDatePresentationBetweenWithDocuments(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Recherche par sujet ou description (insensible à la casse)
    @Query("SELECT p FROM Presentation p WHERE LOWER(p.sujet) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Presentation> searchByTerm(@Param("term") String term);

    // Trouver les présentations d'un utilisateur par statut
    List<Presentation> findByUtilisateurIdUtilisateurAndStatut(Integer idUtilisateur, Presentation.StatutPresentation statut);

    List<Presentation> findAllByOrderByDatePresentationDesc();
}