package com.lahzamihiba.voiceformassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceInstructionDto {
    private Integer order;
    private String field;
    private String type;
    private String speech;
}
