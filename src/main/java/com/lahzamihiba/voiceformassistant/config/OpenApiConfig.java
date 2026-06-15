package com.lahzamihiba.voiceformassistant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI voiceFormAssistantOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Voice Form Assistant API")
                        .description("API d'analyse OCR de formulaires et génération d'instructions vocales")
                        .version("v1")
                        .contact(new Contact().name("Voice Form Assistant Team")));
    }
}
