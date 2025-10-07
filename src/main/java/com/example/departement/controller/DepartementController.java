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
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<List<Departement>> getAllDepartements() {
        try {
            System.out.println("Appel GET /api/departements");
            List<Departement> departements = departementService.getAllDepartements();
            System.out.println("Nombre de départements trouvés: " + departements.size());
            return ResponseEntity.ok(departements);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des départements: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/departements/{id} - Récupérer un département par ID
    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartementById(@PathVariable Integer id) {
        try {
            Optional<Departement> departement = departementService.getDepartementById(id);
            return departement.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du département " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/departements - Créer un nouveau département
    @PostMapping
    public ResponseEntity<?> createDepartement(@Valid @RequestBody Departement departement) {
        try {
            System.out.println("Création d'un département: " + departement.getNomDepartement());
            Departement newDepartement = departementService.createDepartement(departement);
            return ResponseEntity.status(HttpStatus.CREATED).body(newDepartement);
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la création: " + e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Erreur interne lors de la création: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur interne du serveur\"}");
        }
    }

    // PUT /api/departements/{id} - Mettre à jour un département
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartement(@PathVariable Integer id, @Valid @RequestBody Departement departementDetails) {
        try {
            System.out.println("Mise à jour du département " + id);
            Departement updatedDepartement = departementService.updateDepartement(id, departementDetails);
            return ResponseEntity.ok(updatedDepartement);
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Erreur interne lors de la mise à jour: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur interne du serveur\"}");
        }
    }

    // DELETE /api/departements/{id} - Supprimer un département
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartement(@PathVariable Integer id) {
        try {
            System.out.println("Suppression du département " + id);
            departementService.deleteDepartement(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Erreur interne lors de la suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Erreur interne du serveur\"}");
        }
    }

    // GET /api/departements/search?keyword=... - Rechercher des départements
    @GetMapping("/search")
    public ResponseEntity<List<Departement>> searchDepartements(@RequestParam String keyword) {
        try {
            System.out.println("Recherche avec le mot-clé: " + keyword);
            List<Departement> departements = departementService.searchDepartements(keyword);
            return ResponseEntity.ok(departements);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint de test pour vérifier que l'API fonctionne
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("Test endpoint appelé");
        return ResponseEntity.ok("API Départements fonctionne !");
    }
}