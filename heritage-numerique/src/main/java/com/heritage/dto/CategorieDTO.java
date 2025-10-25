package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour une catégorie de contenu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorieDTO {

    private Long id;
    private String nom;
    private String description;
    private String icone;
    private LocalDateTime dateCreation;
}

