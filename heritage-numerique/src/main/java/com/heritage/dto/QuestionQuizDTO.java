package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour représenter une question de quiz avec ses propositions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionQuizDTO {
    
    private Long id;
    private String texteQuestion;
    private String typeQuestion;
    private Integer ordre;
    private Integer points;
    private List<PropositionDTO> propositions;
}

