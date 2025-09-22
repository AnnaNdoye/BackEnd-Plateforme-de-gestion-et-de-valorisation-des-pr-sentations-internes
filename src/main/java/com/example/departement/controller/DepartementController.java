package com.example.departement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.departement.entity.Departement;
import com.example.departement.service.DepartementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/departements")
public class DepartementController {
    
    @Autowired
    private DepartementService departementService;
    
    // GET /api/departements - Récupérer tous les départements
    @GetMapping
    public ResponseEntity<List<Departement>> getAllDepartements() {
        List<Departement> departements = departementService.getAllDepartements();
        return ResponseEntity.ok(departements);
    }
    
    // GET /api/departements/{id} - Récupérer un département par ID
    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartementById(@PathVariable Long id) {
        Optional<Departement> departement = departementService.getDepartementById(id);
        return departement.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/departements - Créer un nouveau département
    @PostMapping
    public ResponseEntity<?> createDepartement(@Valid @RequestBody Departement departement) {
        try {
            Departement newDepartement = departementService.createDepartement(departement);
            return ResponseEntity.status(HttpStatus.CREATED).body(newDepartement);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // PUT /api/departements/{id} - Mettre à jour un département
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartement(@PathVariable Long id, 
                                            @Valid @RequestBody Departement departementDetails) {
        try {
            Departement updatedDepartement = departementService.updateDepartement(id, departementDetails);
            return ResponseEntity.ok(updatedDepartement);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // DELETE /api/departements/{id} - Supprimer un département
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartement(@PathVariable Long id) {
        try {
            departementService.deleteDepartement(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // GET /api/departements/search?keyword=... - Rechercher des départements
    @GetMapping("/search")
    public ResponseEntity<List<Departement>> searchDepartements(@RequestParam String keyword) {
        List<Departement> departements = departementService.searchDepartements(keyword);
        return ResponseEntity.ok(departements);
    }
}