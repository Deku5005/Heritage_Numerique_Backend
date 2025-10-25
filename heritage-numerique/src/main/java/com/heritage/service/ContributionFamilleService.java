package com.heritage.service;

import com.heritage.dto.ContributionMembreDTO;
import com.heritage.dto.ContributionsFamilleDTO;
import com.heritage.entite.Contenu;
import com.heritage.entite.Famille;
import com.heritage.entite.MembreFamille;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des contributions de famille.
 * Fournit des statistiques détaillées sur les contributions de chaque membre.
 */
@Service
public class ContributionFamilleService {

    private final FamilleRepository familleRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final ContenuRepository contenuRepository;

    public ContributionFamilleService(FamilleRepository familleRepository,
                                     MembreFamilleRepository membreFamilleRepository,
                                     ContenuRepository contenuRepository) {
        this.familleRepository = familleRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.contenuRepository = contenuRepository;
    }

    /**
     * Récupère toutes les contributions d'une famille avec statistiques par membre.
     * 
     * @param familleId ID de la famille
     * @return DTO avec les contributions et statistiques
     */
    @Transactional(readOnly = true)
    public ContributionsFamilleDTO getContributionsFamille(Long familleId) {
        // 1. Vérifier que la famille existe
        Famille famille = familleRepository.findById(familleId)
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        // 2. Récupérer tous les membres de la famille
        List<MembreFamille> membres = membreFamilleRepository.findByFamilleId(familleId);

        // 3. Récupérer tous les contenus de la famille
        List<Contenu> contenus = contenuRepository.findByFamilleId(familleId);

        // 4. Calculer les statistiques globales
        int totalContributions = contenus.size();
        int totalContes = (int) contenus.stream().filter(c -> "CONTE".equals(c.getTypeContenu())).count();
        int totalProverbes = (int) contenus.stream().filter(c -> "PROVERBE".equals(c.getTypeContenu())).count();
        int totalArtisanats = (int) contenus.stream().filter(c -> "ARTISANAT".equals(c.getTypeContenu())).count();
        int totalDevinettes = (int) contenus.stream().filter(c -> "DEVINETTE".equals(c.getTypeContenu())).count();

        // 5. Calculer les statistiques par membre
        List<ContributionMembreDTO> contributionsMembres = membres.stream()
                .map(membre -> calculerContributionsMembre(membre, contenus))
                .collect(Collectors.toList());

        // 6. Construire le DTO de réponse
        return ContributionsFamilleDTO.builder()
                .idFamille(famille.getId())
                .nomFamille(famille.getNom())
                .descriptionFamille(famille.getDescription())
                .ethnieFamille(famille.getEthnie())
                .regionFamille(famille.getRegion())
                .totalMembres(membres.size())
                .totalContributions(totalContributions)
                .totalContes(totalContes)
                .totalProverbes(totalProverbes)
                .totalArtisanats(totalArtisanats)
                .totalDevinettes(totalDevinettes)
                .contributionsMembres(contributionsMembres)
                .build();
    }

    /**
     * Calcule les contributions d'un membre spécifique.
     * 
     * @param membre Membre de famille
     * @param contenus Tous les contenus de la famille
     * @return DTO avec les statistiques du membre
     */
    private ContributionMembreDTO calculerContributionsMembre(MembreFamille membre, List<Contenu> contenus) {
        // Filtrer les contenus créés par ce membre
        List<Contenu> contenusMembre = contenus.stream()
                .filter(contenu -> contenu.getAuteur().getId().equals(membre.getUtilisateur().getId()))
                .collect(Collectors.toList());

        // Calculer les statistiques par type
        int nombreContes = (int) contenusMembre.stream()
                .filter(c -> "CONTE".equals(c.getTypeContenu()))
                .count();
        
        int nombreProverbes = (int) contenusMembre.stream()
                .filter(c -> "PROVERBE".equals(c.getTypeContenu()))
                .count();
        
        int nombreArtisanats = (int) contenusMembre.stream()
                .filter(c -> "ARTISANAT".equals(c.getTypeContenu()))
                .count();
        
        int nombreDevinettes = (int) contenusMembre.stream()
                .filter(c -> "DEVINETTE".equals(c.getTypeContenu()))
                .count();

        return ContributionMembreDTO.builder()
                .idMembre(membre.getId())
                .idUtilisateur(membre.getUtilisateur().getId())
                .nom(membre.getUtilisateur().getNom())
                .prenom(membre.getUtilisateur().getPrenom())
                .email(membre.getUtilisateur().getEmail())
                .roleFamille(membre.getRoleFamille().toString())
                .lienParente(membre.getLienParente() != null ? membre.getLienParente() : "Non spécifié")
                .totalContributions(contenusMembre.size())
                .nombreContes(nombreContes)
                .nombreProverbes(nombreProverbes)
                .nombreArtisanats(nombreArtisanats)
                .nombreDevinettes(nombreDevinettes)
                .dateAjout(membre.getDateAjout().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .build();
    }

    /**
     * Récupère les contributions d'un membre spécifique dans une famille.
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @return DTO avec les contributions du membre
     */
    @Transactional(readOnly = true)
    public ContributionMembreDTO getContributionsMembre(Long familleId, Long membreId) {
        // Vérifier que le membre existe et appartient à la famille
        MembreFamille membre = membreFamilleRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre non trouvé"));

        if (!membre.getFamille().getId().equals(familleId)) {
            throw new NotFoundException("Ce membre n'appartient pas à cette famille");
        }

        // Récupérer tous les contenus de la famille
        List<Contenu> contenus = contenuRepository.findByFamilleId(familleId);

        // Calculer les contributions du membre
        return calculerContributionsMembre(membre, contenus);
    }
}
