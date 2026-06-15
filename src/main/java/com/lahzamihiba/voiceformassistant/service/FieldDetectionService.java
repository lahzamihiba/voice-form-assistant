package com.lahzamihiba.voiceformassistant.service;

import com.lahzamihiba.voiceformassistant.entity.FieldDescription;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FieldDetectionService {

    private static final Pattern REQUIRED_PATTERN = Pattern.compile("(\\*|obligatoire|required)", Pattern.CASE_INSENSITIVE);

    public List<FieldDescription> detectFields(String ocrText) {
        List<FieldDescription> fields = new ArrayList<>();
        if (ocrText == null || ocrText.isBlank()) {
            return fields;
        }

        String[] lines = ocrText.split("\\R");
        int order = 1;
        boolean firstMeaningfulLineHandled = false;

        for (String rawLine : lines) {
            String line = rawLine == null ? "" : rawLine.trim();
            if (line.isBlank()) {
                continue;
            }

            String lowered = line.toLowerCase(Locale.ROOT);
            if (!firstMeaningfulLineHandled) {
                firstMeaningfulLineHandled = true;
                if (isLikelyFormTitle(lowered)) {
                    continue;
                }
            }
            if (isNonFieldLine(lowered)) {
                continue;
            }

            boolean required = REQUIRED_PATTERN.matcher(lowered).find();
            String fieldName = normalizeFieldName(line);
            String type = inferType(lowered);

            fields.add(FieldDescription.builder()
                    .order(order++)
                    .name(fieldName)
                    .type(type)
                    .required(required)
                    .build());
        }

        return fields;
    }

    private boolean isNonFieldLine(String lowered) {
        return lowered.contains("envoyer")
                || lowered.contains("submit")
                || lowered.contains("annuler")
                || lowered.contains("reset")
                || lowered.length() < 2;
    }

    private boolean isLikelyFormTitle(String lowered) {
        return containsAny(lowered,
                "création de compte",
                "creation de compte",
                "inscription",
                "register",
                "sign up",
                "formulaire");
    }

    private String normalizeFieldName(String line) {
        String withoutMeta = line.replaceAll("(?i)(obligatoire|required)", "")
                .replace("*", "")
                .replace(":", "")
                .trim();

        Matcher matcher = Pattern.compile("^[\\-•\\d.\\s]*(.+)$").matcher(withoutMeta);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return withoutMeta;
    }

    private String inferType(String loweredLine) {
        if (containsAny(loweredLine, "email", "e-mail", "courriel")) {
            return "email";
        }
        if (containsAny(loweredLine, "téléphone", "telephone", "mobile", "phone")) {
            return "tel";
        }
        if (containsAny(loweredLine, "date", "jj/mm", "yyyy", "naissance")) {
            return "date";
        }
        if (containsAny(loweredLine, "mot de passe", "password")) {
            return "password";
        }
        if (containsAny(loweredLine, "choisir", "sélection", "selection", "liste", "dropdown", "select")) {
            return "select";
        }
        if (containsAny(loweredLine, "checkbox", "cocher", "accepter", "conditions")) {
            return "checkbox";
        }
        if (containsAny(loweredLine, "radio", "homme", "femme", "genre")) {
            return "radio";
        }
        if (containsAny(loweredLine, "message", "description", "commentaire", "textarea")) {
            return "textarea";
        }
        return "text";
    }

    private boolean containsAny(String value, String... candidates) {
        for (String candidate : candidates) {
            if (value.contains(candidate)) {
                return true;
            }
        }
        return false;
    }
}
