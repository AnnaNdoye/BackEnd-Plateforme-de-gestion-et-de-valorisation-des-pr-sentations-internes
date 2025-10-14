package com.example.departement.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.departement.entity.Document;
import com.example.departement.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Obtenir tous les documents d'une présentation
    @GetMapping("/presentation/{idPresentation}")
    public ResponseEntity<List<Document>> getDocumentsByPresentation(@PathVariable Integer idPresentation) {
        try {
            List<Document> documents = documentService.getDocumentsByPresentation(idPresentation);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Télécharger un document
    @GetMapping("/{idDocument}/download")
    public ResponseEntity<?> downloadDocument(@PathVariable Integer idDocument) {
        try {
            Document document = documentService.getDocumentById(idDocument)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

            Path filePath = Paths.get(uploadDir).resolve(document.getChemin()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + document.getNom() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Fichier non trouvé ou illisible"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Supprimer un document
    @DeleteMapping("/{idDocument}")
    public ResponseEntity<?> deleteDocument(@PathVariable Integer idDocument) {
        try {
            documentService.deleteDocument(idDocument);
            return ResponseEntity.ok(Map.of("message", "Document supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}