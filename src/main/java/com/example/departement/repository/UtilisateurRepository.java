package com.example.departement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.departement.entity.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    // Vérifier l'existence par email
    boolean existsByEmail(String email);

    // Vérifier l'existence par matricule
    boolean existsByMatricule(String matricule);

    // Trouver par email
    Optional<Utilisateur> findByEmail(String email);
}
