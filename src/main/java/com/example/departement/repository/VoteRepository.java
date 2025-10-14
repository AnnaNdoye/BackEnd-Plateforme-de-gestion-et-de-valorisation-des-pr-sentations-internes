package com.example.departement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.departement.entity.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    List<Vote> findByPresentationIdPresentation(Integer idPresentation);
    Optional<Vote> findByPresentationIdPresentationAndUtilisateurIdUtilisateur(
        Integer idPresentation, Integer idUtilisateur);
    
    @Query("SELECT AVG(v.note) FROM Vote v WHERE v.presentation.idPresentation = :idPresentation")
    Double getAverageNoteByPresentation(@Param("idPresentation") Integer idPresentation);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.presentation.idPresentation = :idPresentation")
    Long countVotesByPresentation(@Param("idPresentation") Integer idPresentation);
}