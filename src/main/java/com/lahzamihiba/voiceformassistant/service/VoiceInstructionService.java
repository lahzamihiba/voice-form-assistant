package com.lahzamihiba.voiceformassistant.service;

import com.lahzamihiba.voiceformassistant.entity.FieldDescription;
import com.lahzamihiba.voiceformassistant.entity.VoiceInstruction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoiceInstructionService {

    @Value("${app.voice.integration.target:WEB_SPEECH_API}")
    private String speechTarget;

    public List<VoiceInstruction> generateInstructions(List<FieldDescription> fields) {
        return fields.stream()
                .map(this::toInstruction)
                .toList();
    }

    private VoiceInstruction toInstruction(FieldDescription field) {
        String speech = switch (field.getType()) {
            case "email" -> "Veuillez saisir une adresse email valide.";
            case "tel" -> "Veuillez saisir votre numéro de téléphone.";
            case "date" -> "Veuillez sélectionner une date.";
            case "password" -> "Veuillez saisir un mot de passe sécurisé.";
            case "select" -> "Veuillez choisir une option dans la liste.";
            case "checkbox" -> "Veuillez cocher cette case si vous acceptez.";
            case "radio" -> "Veuillez sélectionner une option.";
            case "textarea" -> "Veuillez saisir une réponse détaillée.";
            default -> "Veuillez saisir " + prependArticle(field.getName()) + ".";
        };

        if (field.isRequired()) {
            speech = speech + " Ce champ est obligatoire.";
        }

        return VoiceInstruction.builder()
                .order(field.getOrder())
                .field(field.getName())
                .type(field.getType())
                .speech(speech + " (Compatible " + speechTarget + ")")
                .build();
    }

    private String prependArticle(String fieldName) {
        if (fieldName == null || fieldName.isBlank()) {
            return "la valeur demandée";
        }
        return "votre " + fieldName.toLowerCase();
    }
}
