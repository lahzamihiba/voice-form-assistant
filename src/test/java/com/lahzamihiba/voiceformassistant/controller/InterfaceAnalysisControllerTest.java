package com.lahzamihiba.voiceformassistant.controller;

import com.lahzamihiba.voiceformassistant.entity.FieldDescription;
import com.lahzamihiba.voiceformassistant.entity.VoiceInstruction;
import com.lahzamihiba.voiceformassistant.service.FieldDetectionService;
import com.lahzamihiba.voiceformassistant.service.OcrService;
import com.lahzamihiba.voiceformassistant.service.VoiceInstructionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InterfaceAnalysisController.class)
class InterfaceAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OcrService ocrService;

    @MockBean
    private FieldDetectionService fieldDetectionService;

    @MockBean
    private VoiceInstructionService voiceInstructionService;

    @Test
    void shouldAnalyzeImageAndReturnVoiceInstructions() throws Exception {
        when(ocrService.extractText(any())).thenReturn("Création de compte\nNom *\nEmail");
        when(fieldDetectionService.detectFields(any())).thenReturn(List.of(
                FieldDescription.builder().order(1).name("Nom").type("text").required(true).build(),
                FieldDescription.builder().order(2).name("Email").type("email").required(true).build()
        ));
        when(voiceInstructionService.generateInstructions(any())).thenReturn(List.of(
                VoiceInstruction.builder().order(1).field("Nom").type("text").speech("Veuillez saisir votre nom complet.").build(),
                VoiceInstruction.builder().order(2).field("Email").type("email").speech("Veuillez saisir une adresse email valide.").build()
        ));

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "capture.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image".getBytes()
        );

        mockMvc.perform(multipart("/api/interface/analyze").file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formName").value("Création de compte"))
                .andExpect(jsonPath("$.voiceInstructions[0].order").value(1))
                .andExpect(jsonPath("$.voiceInstructions[0].field").value("Nom"))
                .andExpect(jsonPath("$.voiceInstructions[1].type").value("email"));
    }
}
