package com.heritage.service;

import com.heritage.dto.ProverbeDTO;
import com.heritage.entite.Contenu;
import com.heritage.entite.MembreFamille;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.ContenuRepository;
import com.heritage.repository.MembreFamilleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des proverbes de famille.
 * Fournit des informations détaillées sur les proverbes créés par les membres.
 */
@Service
public class ProverbeService {

    private final ContenuRepository contenuRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public ProverbeService(ContenuRepository contenuRepository,
                          MembreFamilleRepository membreFamilleRepository) {
        this.contenuRepository = contenuRepository;
        this.membreFamilleRepository = membreFamilleRepository;
    }

    /**
     * Récupère tous les proverbes d'une famille avec leurs informations détaillées.
     * 
     * @param familleId ID de la famille
     * @return Liste des proverbes avec informations des auteurs
     */
    @Transactional(readOnly = true)
    public List<ProverbeDTO> getProverbesByFamille(Long familleId) {
        List<Contenu> proverbes = contenuRepository.findByFamilleIdAndTypeContenu(familleId, "PROVERBE");
        
        return proverbes.stream()
                .map(this::convertToProverbeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un proverbe spécifique par son ID.
     * 
     * @param proverbeId ID du proverbe
     * @return DTO du proverbe
     */
    @Transactional(readOnly = true)
    public ProverbeDTO getProverbeById(Long proverbeId) {
        Contenu proverbe = contenuRepository.findById(proverbeId)
                .orElseThrow(() -> new NotFoundException("Proverbe non trouvé"));
        
        if (!"PROVERBE".equals(proverbe.getTypeContenu())) {
            throw new NotFoundException("Ce contenu n'est pas un proverbe");
        }
        
        return convertToProverbeDTO(proverbe);
    }

    /**
     * Récupère les proverbes d'un membre spécifique dans une famille.
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre
     * @return Liste des proverbes du membre
     */
    @Transactional(readOnly = true)
    public List<ProverbeDTO> getProverbesByMembre(Long familleId, Long membreId) {
        MembreFamille membre = membreFamilleRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre non trouvé"));

        if (!membre.getFamille().getId().equals(familleId)) {
            throw new NotFoundException("Ce membre n'appartient pas à cette famille");
        }

        List<Contenu> proverbes = contenuRepository.findByFamilleIdAndAuteurIdAndTypeContenu(
                familleId, membre.getUtilisateur().getId(), "PROVERBE");
        
        return proverbes.stream()
                .map(this::convertToProverbeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Contenu (proverbe) en DTO.
     */
    private ProverbeDTO convertToProverbeDTO(Contenu proverbe) {
        MembreFamille membreAuteur = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(proverbe.getAuteur().getId(), proverbe.getFamille().getId())
                .orElse(null);

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "INCONNU";
        
        if (membreAuteur != null) {
            roleAuteur = membreAuteur.getRoleFamille().toString();
            lienParenteAuteur = membreAuteur.getLienParente() != null ? 
                    membreAuteur.getLienParente() : "Non spécifié";
        }

        // Extraire le proverbe, la signification et l'origine de la description
        String[] parts = proverbe.getDescription().split("\n");
        String proverbeText = "";
        String signification = "";
        String origine = "";
        
        for (String part : parts) {
            if (part.startsWith("Origine:")) {
                origine = part.substring("Origine:".length()).trim();
            } else if (part.startsWith("Signification:")) {
                signification = part.substring("Signification:".length()).trim();
            } else if (part.startsWith("Proverbe:")) {
                proverbeText = part.substring("Proverbe:".length()).trim();
            }
        }

        return ProverbeDTO.builder()
                .id(proverbe.getId())
                .titre(proverbe.getTitre())
                .proverbe(proverbeText)
                .signification(signification)
                .origine(origine)
                .nomAuteur(proverbe.getAuteur().getNom())
                .prenomAuteur(proverbe.getAuteur().getPrenom())
                .emailAuteur(proverbe.getAuteur().getEmail())
                .roleAuteur(roleAuteur)
                .lienParenteAuteur(lienParenteAuteur)
                .dateCreation(proverbe.getDateCreation())
                .statut(proverbe.getStatut())
                .urlPhoto(proverbe.getUrlFichier())
                .lieu(proverbe.getLieu())
                .region(proverbe.getRegion())
                .idFamille(proverbe.getFamille().getId())
                .nomFamille(proverbe.getFamille().getNom())
                .build();
    }
}
