package com.heritage.service;

import com.heritage.dto.AjoutMembreRequest;
import com.heritage.dto.MembreFamilleDTO;
import com.heritage.entite.Famille;
import com.heritage.entite.MembreFamille;
import com.heritage.entite.RoleFamille;
import com.heritage.entite.Utilisateur;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.FamilleRepository;
import com.heritage.repository.MembreFamilleRepository;
import com.heritage.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des membres de famille.
 * Gère l'affichage des membres avec leurs informations détaillées.
 */
@Service
public class MembreFamilleService {

    private final MembreFamilleRepository membreFamilleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FamilleRepository familleRepository;
    private final PasswordEncoder passwordEncoder;

    public MembreFamilleService(MembreFamilleRepository membreFamilleRepository,
                               UtilisateurRepository utilisateurRepository,
                               FamilleRepository familleRepository,
                               PasswordEncoder passwordEncoder) {
        this.membreFamilleRepository = membreFamilleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.familleRepository = familleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Récupère tous les membres d'une famille avec leurs informations détaillées.
     * 
     * @param familleId ID de la famille
     * @return Liste des membres avec leurs informations
     */
    @Transactional(readOnly = true)
    public List<MembreFamilleDTO> getMembresByFamille(Long familleId) {
        List<MembreFamille> membres = membreFamilleRepository.findByFamilleId(familleId);
        
        return membres.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un membre spécifique par son ID.
     * 
     * @param membreId ID du membre
     * @return DTO du membre
     */
    @Transactional(readOnly = true)
    public MembreFamilleDTO getMembreById(Long membreId) {
        MembreFamille membre = membreFamilleRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre non trouvé"));
        
        return convertToDTO(membre);
    }

    /**
     * Ajoute manuellement un membre à une famille.
     * 
     * Processus :
     * 1. Vérifier que la famille existe
     * 2. Vérifier que l'utilisateur n'est pas déjà membre
     * 3. Créer l'utilisateur s'il n'existe pas
     * 4. Créer le lien membre_famille
     * 5. Retourner les informations du membre
     * 
     * @param request Requête d'ajout de membre
     * @param adminId ID de l'administrateur qui ajoute le membre
     * @return DTO du membre ajouté
     */
    @Transactional
    public MembreFamilleDTO ajouterMembreManuellement(AjoutMembreRequest request, Long adminId) {
        // 1. Vérifier que la famille existe
        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        // 2. Vérifier que l'administrateur est bien admin de cette famille
        MembreFamille adminMembre = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(adminId, request.getIdFamille())
                .orElseThrow(() -> new BadRequestException("Vous n'êtes pas membre de cette famille"));

        if (!RoleFamille.ADMIN.equals(adminMembre.getRoleFamille())) {
            throw new BadRequestException("Seul l'administrateur peut ajouter des membres manuellement");
        }

        // 3. Vérifier si l'utilisateur existe déjà
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (utilisateur == null) {
            // Créer un nouvel utilisateur
            utilisateur = new Utilisateur();
            utilisateur.setNom(request.getNom());
            utilisateur.setPrenom(request.getPrenom());
            utilisateur.setEmail(request.getEmail());
            utilisateur.setNumeroTelephone(request.getTelephone());
            utilisateur.setEthnie(request.getEthnie());
            utilisateur.setMotDePasse(passwordEncoder.encode("123456")); // Mot de passe par défaut
            utilisateur.setRole("ROLE_MEMBRE");
            utilisateur.setActif(true);
            utilisateur = utilisateurRepository.save(utilisateur);
        } else {
            // Vérifier que l'utilisateur n'est pas déjà membre de cette famille
            boolean dejaMembre = membreFamilleRepository
                    .existsByUtilisateurIdAndFamilleId(utilisateur.getId(), request.getIdFamille());
            
            if (dejaMembre) {
                throw new BadRequestException("Cet utilisateur est déjà membre de cette famille");
            }
        }

        // 4. Créer le lien membre_famille
        MembreFamille membreFamille = new MembreFamille();
        membreFamille.setUtilisateur(utilisateur);
        membreFamille.setFamille(famille);
        membreFamille.setRoleFamille(RoleFamille.valueOf(request.getRoleFamille()));
        membreFamille.setLienParente(request.getLienParente());
        membreFamille = membreFamilleRepository.save(membreFamille);

        // 5. Retourner les informations du membre
        return convertToDTO(membreFamille);
    }

    /**
     * Convertit une entité MembreFamille en DTO.
     * 
     * @param membre Entité à convertir
     * @return DTO
     */
    private MembreFamilleDTO convertToDTO(MembreFamille membre) {
        return MembreFamilleDTO.builder()
                .id(membre.getId())
                .idUtilisateur(membre.getUtilisateur().getId())
                .nom(membre.getUtilisateur().getNom())
                .prenom(membre.getUtilisateur().getPrenom())
                .email(membre.getUtilisateur().getEmail())
                .telephone(membre.getUtilisateur().getNumeroTelephone())
                .ethnie(membre.getUtilisateur().getEthnie())
                .roleFamille(membre.getRoleFamille().toString())
                .lienParente(getLienParenteFromInvitation(membre))
                .dateAjout(membre.getDateAjout())
                .statut("ACCEPTE") // Les membres dans la table sont acceptés
                .idFamille(membre.getFamille().getId())
                .nomFamille(membre.getFamille().getNom())
                .build();
    }

    /**
     * Récupère le lien de parenté depuis l'invitation correspondante.
     * 
     * @param membre Membre de famille
     * @return Lien de parenté ou null si non trouvé
     */
    private String getLienParenteFromInvitation(MembreFamille membre) {
        // Pour l'instant, on retourne le lien de parenté stocké dans MembreFamille
        return membre.getLienParente();
    }

    /**
     * Récupère le rôle d'un membre dans une famille.
     * 
     * @param userId ID de l'utilisateur
     * @param familleId ID de la famille
     * @return Le rôle du membre (ADMIN, EDITEUR, LECTEUR) ou null si pas membre
     */
    public String getRoleMembre(Long userId, Long familleId) {
        MembreFamille membre = membreFamilleRepository.findByUtilisateurIdAndFamilleId(userId, familleId)
                .orElse(null);
        
        if (membre == null) {
            return null;
        }
        
        return membre.getRoleFamille().name();
    }
}
