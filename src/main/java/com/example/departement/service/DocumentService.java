package com.example.departement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.departement.entity.Document;
import com.example.departement.repository.DocumentRepository;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    // Obtenir tous les documents d'une présentation
    public List<Document> getDocumentsByPresentation(Integer idPresentation) {
        return documentRepository.findByPresentationIdPresentation(idPresentation);
    }

    // Obtenir un document par ID
    public Optional<Document> getDocumentById(Integer idDocument) {
        return documentRepository.findById(idDocument);
    }

    // Supprimer un document
    public void deleteDocument(Integer idDocument) {
        documentRepository.deleteById(idDocument);
    }
}