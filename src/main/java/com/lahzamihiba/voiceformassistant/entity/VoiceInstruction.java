package com.lahzamihiba.voiceformassistant.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceInstruction {
    private Integer order;
    private String field;
    private String type;
    private String speech;
}
