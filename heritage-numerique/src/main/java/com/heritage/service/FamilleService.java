package com.heritage.service;

import com.heritage.dto.FamilleDTO;
import com.heritage.dto.FamilleRequest;
import com.heritage.dto.ChangerRoleRequest;
import com.heritage.entite.*;
import com.heritage.entite.MembreFamille;
import com.heritage.exception.NotFoundException;
import com.heritage.exception.UnauthorizedException;
import com.heritage.repository.FamilleRepository;
import com.heritage.repository.MembreFamilleRepository;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des familles.
 */
@Service
public class FamilleService {

    private final FamilleRepository familleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public FamilleService(
            FamilleRepository familleRepository,
            UtilisateurRepository utilisateurRepository,
            MembreFamilleRepository membreFamilleRepository) {
        this.familleRepository = familleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.membreFamilleRepository = membreFamilleRepository;
    }

    /**
     * Crée une nouvelle famille.
     * Le créateur devient automatiquement ADMIN de la famille.
     * 
     * @param request Requête de création
     * @param createurId ID du créateur
     * @return DTO de la famille créée
     */
    @Transactional
    public FamilleDTO createFamille(FamilleRequest request, Long createurId) {
        Utilisateur createur = utilisateurRepository.findById(createurId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));

        Famille famille = new Famille();
        famille.setNom(request.getNom());
        famille.setDescription(request.getDescription());
        famille.setEthnie(request.getEthnie());
        famille.setRegion(request.getRegion());
        famille.setCreateur(createur);

        famille = familleRepository.save(famille);

        // Ajouter le créateur comme membre ADMIN
        MembreFamille membreFamille = new MembreFamille();
        membreFamille.setUtilisateur(createur);
        membreFamille.setFamille(famille);
        membreFamille.setRoleFamille(RoleFamille.ADMIN);
        membreFamilleRepository.save(membreFamille);

        return convertToDTO(famille);
    }

    /**
     * Récupère une famille par son ID.
     * 
     * @param id ID de la famille
     * @return DTO de la famille
     */
    @Transactional(readOnly = true)
    public FamilleDTO getFamilleById(Long id) {
        Famille famille = familleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        return convertToDTO(famille);
    }

    /**
     * Récupère toutes les familles d'un utilisateur.
     * 
     * @param utilisateurId ID de l'utilisateur
     * @return Liste des familles
     */
    @Transactional(readOnly = true)
    public List<FamilleDTO> getFamillesByUtilisateur(Long utilisateurId) {
        List<MembreFamille> membresFamille = membreFamilleRepository.findByUtilisateurId(utilisateurId);
        
        return membresFamille.stream()
                .map(mf -> convertToDTO(mf.getFamille()))
                .collect(Collectors.toList());
    }

    /**
     * Change le rôle d'un membre dans une famille.
     * Seul l'ADMIN de la famille peut effectuer cette action.
     * 
     * @param familleId ID de la famille
     * @param membreId ID du membre dont on veut changer le rôle
     * @param request Requête contenant le nouveau rôle
     * @param adminId ID de l'utilisateur qui effectue la modification (doit être ADMIN)
     * @return DTO du membre modifié
     */
    @Transactional
    public FamilleDTO changerRoleMembre(Long familleId, Long membreId, ChangerRoleRequest request, Long adminId) {
        // Vérifier que l'admin est bien ADMIN de la famille
        MembreFamille adminMembre = membreFamilleRepository.findByUtilisateurIdAndFamilleId(adminId, familleId)
                .orElseThrow(() -> new NotFoundException("Vous n'êtes pas membre de cette famille"));
        
        if (adminMembre.getRoleFamille() != RoleFamille.ADMIN) {
            throw new UnauthorizedException("Seul l'administrateur de la famille peut changer les rôles");
        }

        // Vérifier que le membre existe dans la famille
        MembreFamille membre = membreFamilleRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre non trouvé"));
        
        if (!familleId.equals(membre.getFamille().getId())) {
            throw new NotFoundException("Ce membre n'appartient pas à cette famille");
        }

        // Empêcher un admin de se retirer ses propres droits
        if (membre.getUtilisateur().getId().equals(adminId) && request.getNouveauRole() != RoleFamille.ADMIN) {
            throw new UnauthorizedException("Un administrateur ne peut pas se retirer ses propres droits d'administrateur");
        }

        // Changer le rôle
        membre.setRoleFamille(request.getNouveauRole());
        membreFamilleRepository.save(membre);

        // Retourner les informations de la famille mise à jour
        return getFamilleById(familleId);
    }

    /**
     * Convertit une entité Famille en DTO.
     * 
     * @param famille Entité à convertir
     * @return DTO
     */
    private FamilleDTO convertToDTO(Famille famille) {
        return FamilleDTO.builder()
                .id(famille.getId())
                .nom(famille.getNom())
                .description(famille.getDescription())
                .ethnie(famille.getEthnie())
                .region(famille.getRegion())
                .idCreateur(famille.getCreateur().getId())
                .nomCreateur(famille.getCreateur().getNom() + " " + famille.getCreateur().getPrenom())
                .dateCreation(famille.getDateCreation())
                .nombreMembres(famille.getMembres().size())
                .build();
    }
}


