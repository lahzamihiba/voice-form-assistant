# Voice Form Assistant

Backend Spring Boot pour l'analyse de formulaires via capture d'écran et génération d'instructions vocales.

## Objectif

Analyser une image d'interface utilisateur et générer automatiquement :
- Description des champs du formulaire
- Instructions vocales pour guider l'utilisateur
- API REST pour intégration avec Web Speech API / Azure Speech Services

## Stack Technique

- Java 21
- Spring Boot 3
- Maven
- OCR (Tesseract/Google Vision)
- Lombok
- Swagger/OpenAPI

## Fonctionnalités

- Extraction OCR des composants de formulaires
- Détection automatique des types de champs
- Génération d'instructions vocales
- API REST multipart/form-data
- Gestion complète des exceptions
- Documentation Swagger

## Installation

```bash
mvn clean install
mvn spring-boot:run
```

## Documentation API

La documentation Swagger est disponible à : `http://localhost:8080/swagger-ui.html`
