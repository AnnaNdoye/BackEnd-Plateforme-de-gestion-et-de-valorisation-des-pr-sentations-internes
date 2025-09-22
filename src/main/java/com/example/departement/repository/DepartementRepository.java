package com.example.departement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.departement.entity.Departement;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {
    
    // Recherche par nom (insensible à la casse)
    List<Departement> findByNomContainingIgnoreCase(String nom);
    
    // Recherche par code (insensible à la casse)
    List<Departement> findByCodeContainingIgnoreCase(String code);
    
    // Recherche globale (nom, code ou description)
    @Query("SELECT d FROM Departement d WHERE " +
            "LOWER(d.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Departement> searchByKeyword(@Param("keyword") String keyword);
    
    // Vérifier l'existence par code (pour éviter les doublons)
    boolean existsByCode(String code);
    
    // Vérifier l'existence par nom (pour éviter les doublons)
    boolean existsByNom(String nom);
}