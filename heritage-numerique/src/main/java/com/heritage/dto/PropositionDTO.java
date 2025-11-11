package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour représenter une proposition de réponse à une question de quiz.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropositionDTO {
    
    private Long id;
    private String texteProposition;
    private Boolean estCorrecte;
    private Integer ordre;
}

