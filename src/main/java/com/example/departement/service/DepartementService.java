package com.example.departement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.departement.entity.Departement;
import com.example.departement.repository.DepartementRepository;

@Service
public class DepartementService {
    
    @Autowired
    private DepartementRepository departementRepository;
    
    // Récupérer tous les départements
    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }
    
    // Récupérer un département par ID
    public Optional<Departement> getDepartementById(Long id) {
        return departementRepository.findById(id);
    }
    
    // Créer un nouveau département
    public Departement createDepartement(Departement departement) {
        // Vérifier que le code n'existe pas déjà
        if (departementRepository.existsByCode(departement.getCode())) {
            throw new RuntimeException("Un département avec ce code existe déjà");
        }
        // Vérifier que le nom n'existe pas déjà
        if (departementRepository.existsByNomDepartement(departement.getNomDepartement())) {
            throw new RuntimeException("Un département avec ce nom existe déjà");
        }
        return departementRepository.save(departement);
    }
    
    // Mettre à jour un département
    public Departement updateDepartement(Long id, Departement departementDetails) {
        Optional<Departement> optionalDepartement = departementRepository.findById(id);
        if (optionalDepartement.isPresent()) {
            Departement departement = optionalDepartement.get();
            
            // Vérifier l'unicité du code (sauf pour le département actuel)
            if (!departement.getCode().equals(departementDetails.getCode()) && 
                departementRepository.existsByCode(departementDetails.getCode())) {
                throw new RuntimeException("Un département avec ce code existe déjà");
            }
            
            // Vérifier l'unicité du nom (sauf pour le département actuel)
            if (!departement.getNomDepartement().equals(departementDetails.getNomDepartement()) && 
                departementRepository.existsByNomDepartement(departementDetails.getNomDepartement())) {
                throw new RuntimeException("Un département avec ce nom existe déjà");
            }
            
            departement.setNomDepartement(departementDetails.getNomDepartement());
            departement.setCode(departementDetails.getCode());
            departement.setDescription(departementDetails.getDescription());
            
            return departementRepository.save(departement);
        } else {
            throw new RuntimeException("Département non trouvé avec l'ID: " + id);
        }
    }
    
    // Supprimer un département
    public void deleteDepartement(Long id) {
        if (departementRepository.existsById(id)) {
            departementRepository.deleteById(id);
        } else {
            throw new RuntimeException("Département non trouvé avec l'ID: " + id);
        }
    }
    
    // Rechercher des départements
    public List<Departement> searchDepartements(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDepartements();
        }
        return departementRepository.searchByKeyword(keyword.trim());
    }
}