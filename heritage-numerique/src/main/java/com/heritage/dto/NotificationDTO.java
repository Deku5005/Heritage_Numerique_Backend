package com.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour une notification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private String type; // INVITATION, ACCEPTATION, CONTENU_PUBLIE, etc.
    private String titre;
    private String message;
    private String canal; // EMAIL, SMS, IN_APP
    private Boolean lu;
    private LocalDateTime dateEnvoi;
    private LocalDateTime dateLecture;
    private String lien;
}

