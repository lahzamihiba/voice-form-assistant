package com.lahzamihiba.voiceformassistant.controller;

import com.lahzamihiba.voiceformassistant.dto.AnalysisResponseDto;
import com.lahzamihiba.voiceformassistant.dto.VoiceInstructionDto;
import com.lahzamihiba.voiceformassistant.entity.FieldDescription;
import com.lahzamihiba.voiceformassistant.entity.VoiceInstruction;
import com.lahzamihiba.voiceformassistant.service.FieldDetectionService;
import com.lahzamihiba.voiceformassistant.service.OcrService;
import com.lahzamihiba.voiceformassistant.service.VoiceInstructionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/interface")
@RequiredArgsConstructor
@Tag(name = "Interface Analysis", description = "Analyse OCR d'interfaces de formulaires")
public class InterfaceAnalysisController {

    private final OcrService ocrService;
    private final FieldDetectionService fieldDetectionService;
    private final VoiceInstructionService voiceInstructionService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Analyser une capture d'écran de formulaire",
            description = "Extrait les champs de formulaire via OCR et génère des instructions vocales",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Analyse réussie",
                            content = @Content(schema = @Schema(implementation = AnalysisResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Requête invalide"),
                    @ApiResponse(responseCode = "422", description = "Impossible d'analyser le contenu OCR")
            }
    )
    public AnalysisResponseDto analyze(@RequestPart("image") MultipartFile image) {
        String extractedText = ocrService.extractText(image);
        List<FieldDescription> fields = fieldDetectionService.detectFields(extractedText);
        List<VoiceInstruction> instructions = voiceInstructionService.generateInstructions(fields);

        return AnalysisResponseDto.builder()
                .formName(resolveFormName(extractedText))
                .voiceInstructions(instructions.stream()
                        .map(this::toDto)
                        .toList())
                .build();
    }

    private String resolveFormName(String extractedText) {
        if (extractedText == null || extractedText.isBlank()) {
            return "Formulaire détecté";
        }
        String firstMeaningfulLine = extractedText.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .findFirst()
                .orElse("Formulaire détecté");

        if (firstMeaningfulLine.length() <= 60) {
            return firstMeaningfulLine;
        }
        return "Formulaire détecté";
    }

    private VoiceInstructionDto toDto(VoiceInstruction instruction) {
        return VoiceInstructionDto.builder()
                .order(instruction.getOrder())
                .field(instruction.getField())
                .type(instruction.getType())
                .speech(instruction.getSpeech())
                .build();
    }
}
