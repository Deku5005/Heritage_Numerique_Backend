package com.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer ou modifier une famille.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilleRequest {

    @NotBlank(message = "Le nom de la famille est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    private String nom;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @Size(max = 100, message = "L'ethnie ne peut pas dépasser 100 caractères")
    private String ethnie;

    @Size(max = 100, message = "La région ne peut pas dépasser 100 caractères")
    private String region;
}


