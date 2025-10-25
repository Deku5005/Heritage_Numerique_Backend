package com.heritage.service;

import com.heritage.dto.AjoutMembreArbreRequest;
import com.heritage.dto.ArbreGenealogiqueDTO;
import com.heritage.dto.MembreArbreDTO;
import com.heritage.entite.*;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion de l'arbre généalogique de famille.
 * Chaque famille a un seul arbre généalogique.
 */
@Service
public class ArbreGenealogiqueService {

    private final ArbreGenealogiqueRepository arbreGenealogiqueRepository;
    private final MembreArbreRepository membreArbreRepository;
    private final FamilleRepository familleRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    public ArbreGenealogiqueService(ArbreGenealogiqueRepository arbreGenealogiqueRepository,
                                   MembreArbreRepository membreArbreRepository,
                                   FamilleRepository familleRepository,
                                   MembreFamilleRepository membreFamilleRepository) {
        this.arbreGenealogiqueRepository = arbreGenealogiqueRepository;
        this.membreArbreRepository = membreArbreRepository;
        this.familleRepository = familleRepository;
        this.membreFamilleRepository = membreFamilleRepository;
    }

    /**
     * Récupère l'arbre généalogique d'une famille avec tous ses membres.
     * 
     * @param familleId ID de la famille
     * @return Arbre généalogique complet
     */
    @Transactional(readOnly = true)
    public ArbreGenealogiqueDTO getArbreByFamille(Long familleId) {
        // Vérifier que la famille existe
        Famille famille = familleRepository.findById(familleId)
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        // Récupérer l'arbre généalogique de la famille
        List<ArbreGenealogique> arbres = arbreGenealogiqueRepository.findByFamilleId(familleId);
        ArbreGenealogique arbre = arbres.isEmpty() ? null : arbres.get(0);

        if (arbre == null) {
            // Créer automatiquement l'arbre s'il n'existe pas
            arbre = new ArbreGenealogique();
            arbre.setFamille(famille);
            arbre.setNom("Arbre généalogique de " + famille.getNom());
            arbre.setDescription("Arbre généalogique de la famille " + famille.getNom());
            arbre = arbreGenealogiqueRepository.save(arbre);
        }

        // Récupérer tous les membres de l'arbre triés par âge (du plus grand au plus petit)
        List<MembreArbre> membres = membreArbreRepository.findByArbreIdOrderByDateNaissanceAsc(arbre.getId());

        return ArbreGenealogiqueDTO.builder()
                .id(arbre.getId())
                .nom(arbre.getNom())
                .description(arbre.getDescription())
                .dateCreation(arbre.getDateCreation())
                .nombreMembres(membres.size())
                .idFamille(famille.getId())
                .nomFamille(famille.getNom())
                .membres(membres.stream()
                        .map(this::convertToMembreArbreDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Ajoute un membre à l'arbre généalogique d'une famille.
     * 
     * @param request Requête d'ajout de membre
     * @param auteurId ID de l'utilisateur qui ajoute le membre
     * @return DTO du membre ajouté
     */
    @Transactional
    public MembreArbreDTO ajouterMembreArbre(AjoutMembreArbreRequest request, Long auteurId) {
        // 1. Vérifier que l'utilisateur est membre de la famille avec le rôle ADMIN
        MembreFamille membre = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, request.getIdFamille())
                .orElseThrow(() -> new BadRequestException("Vous n'êtes pas membre de cette famille"));

        if (!RoleFamille.ADMIN.equals(membre.getRoleFamille())) {
            throw new BadRequestException("Seul l'administrateur peut ajouter des membres à l'arbre généalogique");
        }

        // 2. Vérifier que la famille existe
        Famille famille = familleRepository.findById(request.getIdFamille())
                .orElseThrow(() -> new NotFoundException("Famille non trouvée"));

        // 3. Récupérer ou créer l'arbre généalogique
        List<ArbreGenealogique> arbres = arbreGenealogiqueRepository.findByFamilleId(request.getIdFamille());
        ArbreGenealogique arbre = arbres.isEmpty() ? null : arbres.get(0);

        if (arbre == null) {
            // Créer l'arbre généalogique s'il n'existe pas
            arbre = new ArbreGenealogique();
            arbre.setFamille(famille);
            arbre.setNom("Arbre généalogique de " + famille.getNom());
            arbre.setDescription("Arbre généalogique de la famille " + famille.getNom());
            arbre = arbreGenealogiqueRepository.save(arbre);
        }

        // 4. Créer le membre de l'arbre
        MembreArbre membreArbre = new MembreArbre();
        membreArbre.setArbre(arbre);
        
        // Séparer le nom complet en nom et prénom
        String nomComplet = request.getNomComplet();
        String[] parties = nomComplet.split(" ", 2);
        if (parties.length >= 2) {
            membreArbre.setNom(parties[0]);
            membreArbre.setPrenom(parties[1]);
        } else {
            membreArbre.setNom(nomComplet);
            membreArbre.setPrenom("");
        }
        membreArbre.setDateNaissance(request.getDateNaissance());
        membreArbre.setLieuNaissance(request.getLieuNaissance());
        membreArbre.setBiographie(request.getBiographie());
        membreArbre.setRelationFamiliale(request.getRelationFamiliale());

        // 5. Upload de la photo si fournie
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            try {
                String photoPath = uploadFile(request.getPhoto(), "photos");
                membreArbre.setPhotoUrl(photoPath);
            } catch (IOException e) {
                throw new BadRequestException("Erreur lors de l'upload de la photo : " + e.getMessage());
            }
        }

        // 6. Définir les relations familiales si fournies
        if (request.getParent1Id() != null) {
            MembreArbre parent1 = membreArbreRepository.findById(request.getParent1Id())
                    .orElseThrow(() -> new NotFoundException("Parent 1 non trouvé"));
            membreArbre.setPere(parent1);
        }

        if (request.getParent2Id() != null) {
            MembreArbre parent2 = membreArbreRepository.findById(request.getParent2Id())
                    .orElseThrow(() -> new NotFoundException("Parent 2 non trouvé"));
            membreArbre.setMere(parent2);
        }

        // Note: Le champ conjoint n'existe pas dans l'entité MembreArbre
        // Seuls les champs pere, mere et utilisateurLie sont disponibles

        // 7. Sauvegarder le membre
        membreArbre = membreArbreRepository.save(membreArbre);

        // 8. Retourner le DTO
        return convertToMembreArbreDTO(membreArbre);
    }

    /**
     * Récupère un membre spécifique de l'arbre généalogique.
     * 
     * @param membreId ID du membre
     * @return DTO du membre
     */
    @Transactional(readOnly = true)
    public MembreArbreDTO getMembreArbreById(Long membreId) {
        MembreArbre membre = membreArbreRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre de l'arbre non trouvé"));
        
        return convertToMembreArbreDTO(membre);
    }

    /**
     * Convertit une entité MembreArbre en DTO.
     */
    private MembreArbreDTO convertToMembreArbreDTO(MembreArbre membre) {
        return MembreArbreDTO.builder()
                .id(membre.getId())
                .nomComplet(membre.getNom() + " " + membre.getPrenom())
                .dateNaissance(membre.getDateNaissance())
                .lieuNaissance(membre.getLieuNaissance())
                .relationFamiliale("Membre") // Valeur par défaut
                .telephone("") // Champ non disponible
                .email("") // Champ non disponible
                .biographie(membre.getBiographie())
                .photoUrl(membre.getPhotoUrl())
                .nomPere(membre.getPere() != null ? membre.getPere().getNom() + " " + membre.getPere().getPrenom() : null)
                .nomMere(membre.getMere() != null ? membre.getMere().getNom() + " " + membre.getMere().getPrenom() : null)
                .nomUtilisateurLie(null) // Champ non disponible
                .dateCreation(membre.getDateCreation())
                .idFamille(membre.getArbre().getFamille().getId())
                .nomFamille(membre.getArbre().getFamille().getNom())
                .build();
    }

    /**
     * Upload un fichier et retourne son chemin.
     */
    private String uploadFile(MultipartFile file, String subfolder) throws IOException {
        // Créer le dossier s'il n'existe pas
        Path uploadDir = Paths.get(uploadPath, subfolder);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // Sauvegarder le fichier
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return subfolder + "/" + filename;
    }
}