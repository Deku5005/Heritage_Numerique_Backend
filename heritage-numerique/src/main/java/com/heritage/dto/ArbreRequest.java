package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer un arbre généalogique.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArbreRequest {

    @NotNull(message = "L'ID de la famille est obligatoire")
    private Long idFamille;

    @NotBlank(message = "Le nom de l'arbre est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
    private String nom;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;
}
