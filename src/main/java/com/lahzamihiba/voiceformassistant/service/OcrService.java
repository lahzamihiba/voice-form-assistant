package com.lahzamihiba.voiceformassistant.service;

import com.lahzamihiba.voiceformassistant.exception.InvalidFileException;
import com.lahzamihiba.voiceformassistant.exception.OcrProcessingException;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class OcrService {

    @Value("${app.ocr.tesseract.datapath:}")
    private String tesseractDatapath;

    @Value("${app.ocr.tesseract.language:fra+eng}")
    private String language;

    public String extractText(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new InvalidFileException("Le fichier image est obligatoire");
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileException("Le fichier fourni doit être une image");
        }

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("ocr-", imageFile.getOriginalFilename());
            imageFile.transferTo(tempFile);

            ITesseract tesseract = new Tesseract();
            if (!tesseractDatapath.isBlank()) {
                tesseract.setDatapath(tesseractDatapath);
            }
            tesseract.setLanguage(language);

            return tesseract.doOCR(tempFile.toFile());
        } catch (IOException | TesseractException ex) {
            throw new OcrProcessingException("Échec de l'analyse OCR de l'image", ex);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ioException) {
                    log.warn("Impossible de supprimer le fichier temporaire OCR: {}", tempFile, ioException);
                }
            }
        }
    }
}
