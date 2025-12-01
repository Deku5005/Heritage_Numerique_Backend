package com.heritage.service;

import com.heritage.dto.AjoutMembreArbreRequest;
import com.heritage.dto.ArbreGenealogiqueDTO;
import com.heritage.dto.ArbreGenealogiqueHierarchiqueDTO;
import com.heritage.dto.MembreArbreDTO;
import com.heritage.dto.NoeudArbreDTO;
import com.heritage.entite.*;
import com.heritage.exception.BadRequestException;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour la gestion de l'arbre généalogique de famille.
 * Chaque famille a un seul arbre généalogique.
 * Adapté pour utiliser FileStorageService tout en conservant le format de chemin DB souhaité (court).
 */
@Service
public class ArbreGenealogiqueService {

    private final ArbreGenealogiqueRepository arbreGenealogiqueRepository;
    private final MembreArbreRepository membreArbreRepository;
    private final FamilleRepository familleRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final UtilisateurRepository utilisateurRepository;

    // 🔑 Injection du service de stockage centralisé
    private final FileStorageService fileStorageService;

    // Note: La variable @Value("${app.upload.path:uploads}") et la méthode uploadFile locale sont supprimées.

    public ArbreGenealogiqueService(ArbreGenealogiqueRepository arbreGenealogiqueRepository,
                                    MembreArbreRepository membreArbreRepository,
                                    FamilleRepository familleRepository,
                                    MembreFamilleRepository membreFamilleRepository,
                                    UtilisateurRepository utilisateurRepository,
                                    FileStorageService fileStorageService) { // Injection du service
        this.arbreGenealogiqueRepository = arbreGenealogiqueRepository;
        this.membreArbreRepository = membreArbreRepository;
        this.familleRepository = familleRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.fileStorageService = fileStorageService; // Initialisation
    }

    /**
     * Récupère l'arbre généalogique d'une famille avec tous ses membres.
     * * @param familleId ID de la famille
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

            // Définir le créateur de l'arbre (utiliser le premier membre ADMIN de la famille)
            Utilisateur createur = famille.getMembres().stream()
                    .filter(m -> m.getRoleFamille() == RoleFamille.ADMIN)
                    .map(MembreFamille::getUtilisateur)
                    .findFirst()
                    .orElse(famille.getMembres().get(0).getUtilisateur()); // Fallback sur le premier membre
            arbre.setCreateur(createur);

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
     * Récupère l'arbre généalogique d'une famille sous forme hiérarchique.
     * Structure optimisée pour l'affichage dans Flutter (style MyHeritage).
     *
     * @param familleId ID de la famille
     * @return Arbre généalogique hiérarchique
     */
    @Transactional(readOnly = true)
    public ArbreGenealogiqueHierarchiqueDTO getArbreHierarchiqueByFamille(Long familleId) {
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

            Utilisateur createur = famille.getMembres().stream()
                    .filter(m -> m.getRoleFamille() == RoleFamille.ADMIN)
                    .map(MembreFamille::getUtilisateur)
                    .findFirst()
                    .orElse(famille.getMembres().get(0).getUtilisateur());
            arbre.setCreateur(createur);

            arbre = arbreGenealogiqueRepository.save(arbre);
        }

        // Récupérer tous les membres de l'arbre
        List<MembreArbre> tousMembres = membreArbreRepository.findByArbreId(arbre.getId());

        if (tousMembres.isEmpty()) {
            return ArbreGenealogiqueHierarchiqueDTO.builder()
                    .id(arbre.getId())
                    .nom(arbre.getNom())
                    .description(arbre.getDescription())
                    .dateCreation(arbre.getDateCreation())
                    .dateModification(arbre.getDateModification())
                    .idFamille(famille.getId())
                    .nomFamille(famille.getNom())
                    .idCreateur(arbre.getCreateur() != null ? arbre.getCreateur().getId() : null)
                    .nomCreateur(arbre.getCreateur() != null ?
                            arbre.getCreateur().getNom() + " " + arbre.getCreateur().getPrenom() : null)
                    .racines(new ArrayList<>())
                    .nombreMembres(0)
                    .nombreGenerations(0)
                    .build();
        }

        // Créer une map pour accès rapide aux membres par ID
        Map<Long, MembreArbre> membresMap = tousMembres.stream()
                .collect(Collectors.toMap(MembreArbre::getId, m -> m));

        // Identifier les racines (membres sans parents)
        List<MembreArbre> racines = tousMembres.stream()
                .filter(m -> m.getPere() == null && m.getMere() == null)
                .collect(Collectors.toList());

        // Si aucune racine n'est trouvée, prendre le membre le plus ancien comme racine
        if (racines.isEmpty()) {
            racines = tousMembres.stream()
                    .sorted((m1, m2) -> {
                        if (m1.getDateNaissance() != null && m2.getDateNaissance() != null) {
                            return m1.getDateNaissance().compareTo(m2.getDateNaissance());
                        }
                        return 0;
                    })
                    .limit(1)
                    .collect(Collectors.toList());
        }

        // Construire la structure hiérarchique récursive
        List<NoeudArbreDTO> racinesDTO = new ArrayList<>();
        int maxNiveau = 0;

        for (MembreArbre racine : racines) {
            NoeudArbreDTO noeud = construireNoeudRecursif(racine, membresMap, 0, 0.0, 0.0);
            racinesDTO.add(noeud);
            maxNiveau = Math.max(maxNiveau, calculerNiveauMax(noeud));
        }

        // Calculer les positions pour le layout
        calculerPositions(racinesDTO, 0, 0.0);

        return ArbreGenealogiqueHierarchiqueDTO.builder()
                .id(arbre.getId())
                .nom(arbre.getNom())
                .description(arbre.getDescription())
                .dateCreation(arbre.getDateCreation())
                .dateModification(arbre.getDateModification())
                .idFamille(famille.getId())
                .nomFamille(famille.getNom())
                .idCreateur(arbre.getCreateur() != null ? arbre.getCreateur().getId() : null)
                .nomCreateur(arbre.getCreateur() != null ?
                        arbre.getCreateur().getNom() + " " + arbre.getCreateur().getPrenom() : null)
                .racines(racinesDTO)
                .nombreMembres(tousMembres.size())
                .nombreGenerations(maxNiveau + 1)
                .idMembreRacinePrincipal(racines.isEmpty() ? null : racines.get(0).getId())
                .build();
    }

    /**
     * Construit récursivement un nœud de l'arbre avec ses enfants.
     */
    private NoeudArbreDTO construireNoeudRecursif(MembreArbre membre,
                                                  Map<Long, MembreArbre> membresMap,
                                                  int niveau,
                                                  double posX,
                                                  double posY) {
        // Récupérer tous les enfants de ce membre
        List<MembreArbre> enfants = membreArbreRepository.findByPereIdOrMereId(membre.getId(), membre.getId());

        // Construire les nœuds enfants
        List<NoeudArbreDTO> enfantsDTO = new ArrayList<>();
        double enfantPosX = posX;

        for (MembreArbre enfant : enfants) {
            NoeudArbreDTO enfantNoeud = construireNoeudRecursif(enfant, membresMap, niveau + 1, enfantPosX, posY + 1.0);
            enfantsDTO.add(enfantNoeud);
            enfantPosX += 1.0; // Espacement horizontal entre enfants
        }

        return NoeudArbreDTO.builder()
                .id(membre.getId())
                .nom(membre.getNom())
                .prenom(membre.getPrenom())
                .nomComplet(membre.getNomComplet())
                .sexe(membre.getSexe())
                .dateNaissance(membre.getDateNaissance())
                .dateDeces(membre.getDateDeces())
                .lieuNaissance(membre.getLieuNaissance())
                .lieuDeces(membre.getLieuDeces())
                .biographie(membre.getBiographie())
                .photoUrl(membre.getPhotoUrl())
                .relationFamiliale(membre.getRelationFamiliale())
                .idPere(membre.getPere() != null ? membre.getPere().getId() : null)
                .idMere(membre.getMere() != null ? membre.getMere().getId() : null)
                .enfants(enfantsDTO)
                .nombreEnfants(enfantsDTO.size())
                .niveau(niveau)
                .positionX(posX)
                .positionY(posY)
                .build();
    }

    /**
     * Calcule le niveau maximum dans l'arbre.
     */
    private int calculerNiveauMax(NoeudArbreDTO noeud) {
        if (noeud.getEnfants() == null || noeud.getEnfants().isEmpty()) {
            return noeud.getNiveau();
        }

        int maxNiveau = noeud.getNiveau();
        for (NoeudArbreDTO enfant : noeud.getEnfants()) {
            maxNiveau = Math.max(maxNiveau, calculerNiveauMax(enfant));
        }
        return maxNiveau;
    }

    /**
     * Calcule les positions X et Y pour chaque nœud pour un meilleur layout.
     */
    private void calculerPositions(List<NoeudArbreDTO> noeuds, int niveau, double startX) {
        double currentX = startX;

        for (NoeudArbreDTO noeud : noeuds) {
            noeud.setPositionX(currentX);
            noeud.setPositionY(niveau);

            if (noeud.getEnfants() != null && !noeud.getEnfants().isEmpty()) {
                // Calculer la largeur totale des enfants
                double largeurEnfants = calculerLargeur(noeud.getEnfants());
                double startXEnfants = currentX - (largeurEnfants / 2.0) + 0.5;

                calculerPositions(noeud.getEnfants(), niveau + 1, startXEnfants);
            }

            currentX += 1.0;
        }
    }

    /**
     * Calcule la largeur totale d'une liste de nœuds.
     */
    private double calculerLargeur(List<NoeudArbreDTO> noeuds) {
        if (noeuds == null || noeuds.isEmpty()) {
            return 1.0;
        }

        double largeur = 0.0;
        for (NoeudArbreDTO noeud : noeuds) {
            if (noeud.getEnfants() != null && !noeud.getEnfants().isEmpty()) {
                largeur += calculerLargeur(noeud.getEnfants());
            } else {
                largeur += 1.0;
            }
        }
        return Math.max(largeur, noeuds.size());
    }

    /**
     * Ajoute un membre à l'arbre généalogique d'une famille.
     * * @param request Requête d'ajout de membre
     * @param auteurId ID de l'utilisateur qui ajoute le membre
     * @return DTO du membre ajouté
     */
    @Transactional
    public MembreArbreDTO ajouterMembreArbre(AjoutMembreArbreRequest request, Long auteurId) {
        // 1. Vérifier que l'utilisateur est membre de la famille avec le rôle ADMIN
        MembreFamille membre = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(auteurId, request.getIdFamille())
                .orElseThrow(() -> new BadRequestException("Vous n'êtes pas membre de cette famille"));

        if (!membre.getRoleFamille().canWrite()) {
            throw new BadRequestException("Seul l'administrateur ou l'éditeur peut ajouter des membres à l'arbre généalogique");
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

            // Définir le créateur de l'arbre
            Utilisateur createur = utilisateurRepository.findById(auteurId)
                    .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé"));
            arbre.setCreateur(createur);

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
        // Gérer différents formats de date
        LocalDate dateNaissance = parseDateNaissance(request.getDateNaissance());
        membreArbre.setDateNaissance(dateNaissance);
        membreArbre.setLieuNaissance(request.getLieuNaissance());
        membreArbre.setBiographie(request.getBiographie());
        membreArbre.setRelationFamiliale(request.getRelationFamiliale());

        // 5. Upload de la photo si fournie
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            // 🔑 ADAPTATION: Utiliser la même logique que ContenuCreationService
            // La méthode handleFileUpload détermine automatiquement le sous-dossier "images"
            // et retourne le chemin complet avec /uploads/ pour stockage en DB
            String urlPhoto = handleFileUpload(request.getPhoto(), "photo");
            membreArbre.setPhotoUrl(urlPhoto);
        }

        // 6. Définir les relations familiales si fournies
        // --- Gestion du Parent 1 (Père) ---
// 1. On vérifie que la chaîne n'est ni null, ni vide, ni la chaîne "0" (envoyée par Flutter si "Non spécifié")
        if (request.getParent1Id() != null && !request.getParent1Id().isEmpty() && !request.getParent1Id().equals("0")) {
            try {
                // 2. Conversion de la String en Long
                Long parent1LongId = Long.parseLong(request.getParent1Id());

                // 3. Récupération et assignation du membre
                MembreArbre parent1 = membreArbreRepository.findById(parent1LongId)
                        .orElseThrow(() -> new NotFoundException("Parent 1 non trouvé. ID: " + request.getParent1Id()));
                membreArbre.setPere(parent1);

            } catch (NumberFormatException e) {
                // Gérer l'exception si la chaîne n'est pas un nombre (sécurité)
                throw new BadRequestException("ID du Parent 1 non valide : " + request.getParent1Id());
            }
        }

// --- Gestion du Parent 2 (Mère) ---
        if (request.getParent2Id() != null && !request.getParent2Id().isEmpty() && !request.getParent2Id().equals("0")) {
            try {
                // 2. Conversion de la String en Long
                Long parent2LongId = Long.parseLong(request.getParent2Id());

                // 3. Récupération et assignation du membre
                MembreArbre parent2 = membreArbreRepository.findById(parent2LongId)
                        .orElseThrow(() -> new NotFoundException("Parent 2 non trouvé. ID: " + request.getParent2Id()));
                membreArbre.setMere(parent2);

            } catch (NumberFormatException e) {
                throw new BadRequestException("ID du Parent 2 non valide : " + request.getParent2Id());
            }
        }

        // 7. Sauvegarder le membre
        membreArbre = membreArbreRepository.save(membreArbre);

        // 8. Retourner le DTO
        return convertToMembreArbreDTO(membreArbre);
    }

    /**
     * Récupère un membre spécifique de l'arbre généalogique.
     * * @param membreId ID du membre
     * @return DTO du membre
     */
    @Transactional(readOnly = true)
    public MembreArbreDTO getMembreArbreById(Long membreId) {
        MembreArbre membre = membreArbreRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre de l'arbre non trouvé"));

        return convertToMembreArbreDTO(membre);
    }

    /**
     * Récupère tous les membres de l'arbre généalogique liés à un membre spécifique.
     * Inclut les descendants (enfants, petits-enfants, etc.), les ascendants (parents, grands-parents, etc.)
     * et les frères et sœurs.
     * L'ordre d'affichage est : Parent1 (père) -> Parent2 (mère) -> Membre de référence -> Autres membres
     *
     * @param membreId ID du membre de référence
     * @return Liste de tous les membres liés, triée par ordre de relation
     */
    @Transactional(readOnly = true)
    public List<MembreArbreDTO> getTousMembresLies(Long membreId) {
        MembreArbre membreReference = membreArbreRepository.findById(membreId)
                .orElseThrow(() -> new NotFoundException("Membre de l'arbre non trouvé avec l'ID: " + membreId));

        Set<Long> membresVisites = new HashSet<>();
        Set<MembreArbre> membresLies = new HashSet<>();

        // Récupérer récursivement tous les membres liés
        collecterMembresLiesRecursif(membreReference, membresVisites, membresLies);

        // Trier les membres : Parent1 (père) -> Parent2 (mère) -> Membre de référence -> Autres
        List<MembreArbreDTO> membresTriees = new ArrayList<>();

        // 1. Ajouter le père (parent1) s'il existe
        if (membreReference.getPere() != null && membresLies.contains(membreReference.getPere())) {
            membresTriees.add(convertToMembreArbreDTO(membreReference.getPere()));
        }

        // 2. Ajouter la mère (parent2) s'il existe
        if (membreReference.getMere() != null && membresLies.contains(membreReference.getMere())) {
            membresTriees.add(convertToMembreArbreDTO(membreReference.getMere()));
        }

        // 3. Ajouter le membre de référence
        membresTriees.add(convertToMembreArbreDTO(membreReference));

        // 4. Ajouter tous les autres membres (en excluant père, mère et membre de référence)
        membresLies.stream()
                .filter(m -> !m.getId().equals(membreId) &&
                        (membreReference.getPere() == null || !m.getId().equals(membreReference.getPere().getId())) &&
                        (membreReference.getMere() == null || !m.getId().equals(membreReference.getMere().getId())))
                .map(this::convertToMembreArbreDTO)
                .sorted((m1, m2) -> {
                    // Trier les autres membres par date de naissance (du plus ancien au plus récent)
                    if (m1.getDateNaissance() != null && m2.getDateNaissance() != null) {
                        return m1.getDateNaissance().compareTo(m2.getDateNaissance());
                    }
                    return 0;
                })
                .forEach(membresTriees::add);

        return membresTriees;
    }

    /**
     * Méthode récursive pour collecter tous les membres liés à un membre donné.
     *
     * @param membre Membre actuel
     * @param membresVisites Set des IDs de membres déjà visités (éviter les boucles infinies)
     * @param membresLies Set des membres liés collectés
     */
    private void collecterMembresLiesRecursif(MembreArbre membre, Set<Long> membresVisites, Set<MembreArbre> membresLies) {
        // Éviter de revisiter un membre déjà traité
        if (membre == null || membresVisites.contains(membre.getId())) {
            return;
        }

        // Marquer comme visité et ajouter à la collection
        membresVisites.add(membre.getId());
        membresLies.add(membre);

        // 1. Récupérer les ascendants (parents)
        if (membre.getPere() != null) {
            collecterMembresLiesRecursif(membre.getPere(), membresVisites, membresLies);
        }
        if (membre.getMere() != null) {
            collecterMembresLiesRecursif(membre.getMere(), membresVisites, membresLies);
        }

        // 2. Récupérer les descendants (enfants)
        List<MembreArbre> enfants = membreArbreRepository.findByPereIdOrMereId(membre.getId(), membre.getId());
        for (MembreArbre enfant : enfants) {
            collecterMembresLiesRecursif(enfant, membresVisites, membresLies);
        }

        // 3. Récupérer les frères et sœurs (qui partagent au moins un parent)
        if (membre.getPere() != null) {
            List<MembreArbre> fratrie = membreArbreRepository.findByPereId(membre.getPere().getId());
            for (MembreArbre frere : fratrie) {
                if (!frere.getId().equals(membre.getId())) {
                    collecterMembresLiesRecursif(frere, membresVisites, membresLies);
                }
            }
        }
        if (membre.getMere() != null) {
            List<MembreArbre> fratrie = membreArbreRepository.findByMereId(membre.getMere().getId());
            for (MembreArbre frere : fratrie) {
                if (!frere.getId().equals(membre.getId())) {
                    collecterMembresLiesRecursif(frere, membresVisites, membresLies);
                }
            }
        }
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
                .relationFamiliale(membre.getRelationFamiliale()) // Utiliser la vraie relation familiale
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
     * Récupère les informations d'un membre de famille.
     */
    public MembreFamille getMembreFamille(Long utilisateurId, Long familleId) {
        return membreFamilleRepository.findByUtilisateurIdAndFamilleId(utilisateurId, familleId).orElse(null);
    }

    /**
     * Parse une date de naissance en gérant différents formats.
     */
    private LocalDate parseDateNaissance(LocalDate dateNaissance) {
        return dateNaissance; // Si c'est déjà un LocalDate, le retourner tel quel
    }

    /**
     * Gère la logique de détermination du sous-dossier et appelle le service de stockage (avec validation).
     * Utilisée pour tous les fichiers qui nécessitent une validation de type de fichier (images, vidéos).
     * Même logique que ContenuCreationService pour assurer la cohérence.
     */
    private String handleFileUpload(MultipartFile fichier, String type) {
        // 1. Déterminer le sous-dossier de destination (Logique métier)
        // Pour les images, toutes vont dans le dossier 'images' (comme pour les contenus)
        String sousDossier = "images";

        // 2. Déléger l'opération de stockage au service externe (Contrat technique)
        try {
            // L'appel à storeFile() dans FileStorageService retourne /uploads/images/uuid.jpg
            // Ce chemin complet est stocké en DB (comme pour les contenus)
            return fileStorageService.storeFile(fichier, sousDossier);
        } catch (IOException e) {
            // 3. Gérer l'exception de stockage selon les règles du service métier
            throw new BadRequestException("Erreur lors de la sauvegarde du fichier: " + e.getMessage());
        }
    }

    /**
     * Parse une date de naissance à partir d'une chaîne en gérant différents formats.
     */
    private LocalDate parseDateNaissance(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new BadRequestException("La date de naissance est obligatoire");
        }

        // Formats supportés
        String[] formats = {
                "yyyy-MM-dd",      // 1990-05-12
                "dd/MM/yyyy",      // 12/05/1990
                "dd-MM/yyyy",      // 12-05-1990
                "MM/dd/yyyy",      // 05/12/1990
                "yyyy/MM/dd"       // 1990/05/12
        };

        for (String format : formats) {
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(format);
                return LocalDate.parse(dateString, formatter);
            } catch (Exception e) {
                // Continuer avec le format suivant
            }
        }

        throw new BadRequestException("Format de date non supporté. Utilisez: YYYY-MM-DD, DD/MM/YYYY, DD-MM-YYYY, MM/DD/YYYY ou YYYY/MM/DD");
    }
}