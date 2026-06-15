package com.lahzamihiba.voiceformassistant.service;

import com.lahzamihiba.voiceformassistant.entity.FieldDescription;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldDetectionServiceTest {

    private final FieldDetectionService fieldDetectionService = new FieldDetectionService();

    @Test
    void shouldDetectFieldOrderTypeAndRequired() {
        String ocr = "Création de compte\nNom*\nEmail obligatoire\nDate de naissance\nEnvoyer";

        List<FieldDescription> fields = fieldDetectionService.detectFields(ocr);

        assertEquals(4, fields.size());
        assertEquals("Nom", fields.get(1).getName());
        assertEquals("email", fields.get(2).getType());
        assertTrue(fields.get(2).isRequired());
        assertEquals("date", fields.get(3).getType());
    }
}
