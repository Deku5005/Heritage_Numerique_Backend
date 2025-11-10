package com.heritage.service;

import com.heritage.dto.*;
import com.heritage.entite.Contenu;
import com.heritage.entite.Famille;
import com.heritage.entite.Quiz;
import com.heritage.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour le dashboard du super-admin.
 * Fournit toutes les statistiques et données globales de l'application.
 */
@Service
public class SuperAdminDashboardService {

    private final UtilisateurRepository utilisateurRepository;
    private final FamilleRepository familleRepository;
    private final ContenuRepository contenuRepository;
    private final QuizRepository quizRepository;
    private final MembreFamilleRepository membreFamilleRepository;

    public SuperAdminDashboardService(UtilisateurRepository utilisateurRepository,
                                      FamilleRepository familleRepository,
                                      ContenuRepository contenuRepository,
                                      QuizRepository quizRepository,
                                      MembreFamilleRepository membreFamilleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.familleRepository = familleRepository;
        this.contenuRepository = contenuRepository;
        this.quizRepository = quizRepository;
        this.membreFamilleRepository = membreFamilleRepository;
    }

    /**
     * Récupère le dashboard complet du super-admin.
     * * @return Dashboard complet avec toutes les statistiques
     */
    @Transactional(readOnly = true)
    public SuperAdminDashboardDTO getDashboardComplet() {
        // Statistiques générales
        long nombreUtilisateurs = utilisateurRepository.count();
        long nombreFamilles = familleRepository.count();
        long nombreContes = contenuRepository.countByTypeContenu("CONTE");
        long nombreArtisanats = contenuRepository.countByTypeContenu("ARTISANAT");
        long nombreProverbes = contenuRepository.countByTypeContenu("PROVERBE");
        long nombreDevinettes = contenuRepository.countByTypeContenu("DEVINETTE");
        long nombreQuizPublics = quizRepository.count();

        // Contenus récents
        List<ContenuRecentDTO> contenusRecents = contenuRepository.findTop10ByOrderByDateCreationDesc()
                .stream()
                .map(this::convertirContenuRecent)
                .collect(Collectors.toList());

        // Familles récentes
        List<FamilleRecenteDTO> famillesRecentes = familleRepository.findTop10ByOrderByDateCreationDesc()
                .stream()
                .map(this::convertirFamilleRecente)
                .collect(Collectors.toList());

        return SuperAdminDashboardDTO.builder()
                .nombreUtilisateurs(nombreUtilisateurs)
                .nombreFamilles(nombreFamilles)
                .nombreContes(nombreContes)
                .nombreArtisanats(nombreArtisanats)
                .nombreProverbes(nombreProverbes)
                .nombreDevinettes(nombreDevinettes)
                .nombreQuizPublics(nombreQuizPublics)
                .contenusRecents(contenusRecents)
                .famillesRecentes(famillesRecentes)
                .build();
    }

    /**
     * Récupère toutes les familles de l'application.
     * * @return Liste de toutes les familles
     */
    @Transactional(readOnly = true)
    public List<FamilleSuperAdminDTO> getAllFamilles() {
        return familleRepository.findAll()
                .stream()
                .map(this::convertirFamilleSuperAdmin)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les quiz publics créés par le super-admin.
     * * @return Liste des quiz publics
     */
    @Transactional(readOnly = true)
    public List<QuizPublicDTO> getQuizPublics() {
        return quizRepository.findAll()
                .stream()
                .map(this::convertirQuizPublic)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les contes de l'application.
     * * @return Liste de tous les contes
     */
    @Transactional(readOnly = true)
    public List<ContenuGlobalDTO> getAllContes() {
        return contenuRepository.findByTypeContenu("CONTE")
                .stream()
                .map(this::convertirContenuGlobal)
                .collect(Collectors.toList());
    }

    /**
     * Récupère le détail complet d’un conte spécifique.
     *
     * @param id ID du conte
     * @return Détails du conte
     */
    @Transactional(readOnly = true)
    public ContenuGlobalDTO getDetailConte(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conte introuvable avec l'ID " + id));

        return convertirContenuGlobal(contenu);
    }

    /**
     * Récupère tous les artisanats de l'application.
     * * @return Liste de tous les artisanats
     */
    @Transactional(readOnly = true)
    public List<ContenuGlobalDTO> getAllArtisanats() {
        return contenuRepository.findByTypeContenu("ARTISANAT")
                .stream()
                .map(this::convertirContenuGlobal)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les proverbes de l'application.
     * * @return Liste de tous les proverbes
     */
    @Transactional(readOnly = true)
    public List<ContenuGlobalDTO> getAllProverbes() {
        return contenuRepository.findByTypeContenu("PROVERBE")
                .stream()
                .map(this::convertirContenuGlobal)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les devinettes de l'application.
     * * @return Liste de toutes les devinettes
     */
    @Transactional(readOnly = true)
    public List<ContenuGlobalDTO> getAllDevinettes() {
        return contenuRepository.findByTypeContenu("DEVINETTE")
                .stream()
                .map(this::convertirContenuGlobal)
                .collect(Collectors.toList());
    }

    /**
     * Supprime une devinette spécifique par son ID.
     *
     * @param id ID de la devinette à supprimer
     */
    @Transactional
    public void supprimerDevinette(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devinette introuvable avec l'ID " + id));

        if (!"DEVINETTE".equalsIgnoreCase(contenu.getTypeContenu())) {
            throw new RuntimeException("Ce contenu n'est pas une devinette !");
        }

        contenuRepository.delete(contenu);
    }

    /**
     * Supprime un proverbe spécifique par son ID. 👈 AJOUT DE LA LOGIQUE DE SUPPRESSION
     *
     * @param id ID du proverbe à supprimer
     */
    @Transactional
    public void supprimerProverbe(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proverbe introuvable avec l'ID " + id));

        // Vérification que le contenu est bien un proverbe avant de supprimer
        if (!"PROVERBE".equalsIgnoreCase(contenu.getTypeContenu())) {
            throw new RuntimeException("Ce contenu n'est pas un proverbe !");
        }

        contenuRepository.delete(contenu);
    }


    // Méthodes de conversion privées
    private ContenuRecentDTO convertirContenuRecent(Contenu contenu) {
        return ContenuRecentDTO.builder()
                .id(contenu.getId())
                .titre(contenu.getTitre())
                .typeContenu(contenu.getTypeContenu())
                .dateCreation(contenu.getDateCreation())
                .nomCreateur(contenu.getAuteur().getNom())
                .prenomCreateur(contenu.getAuteur().getPrenom())
                .nomFamille(contenu.getFamille() != null ? contenu.getFamille().getNom() : null)
                .build();
    }

    private FamilleRecenteDTO convertirFamilleRecente(Famille famille) {
        return FamilleRecenteDTO.builder()
                .id(famille.getId())
                .nom(famille.getNom())
                .description(famille.getDescription())
                .ethnie(famille.getEthnie())
                .region(famille.getRegion())
                .dateCreation(famille.getDateCreation())
                .nomAdmin(famille.getCreateur().getNom())
                .prenomAdmin(famille.getCreateur().getPrenom())
                .build();
    }

    private FamilleSuperAdminDTO convertirFamilleSuperAdmin(Famille famille) {
        return FamilleSuperAdminDTO.builder()
                .id(famille.getId())
                .nom(famille.getNom())
                .description(famille.getDescription())
                .ethnie(famille.getEthnie())
                .region(famille.getRegion())
                .dateCreation(famille.getDateCreation())
                .nomAdmin(famille.getCreateur().getNom())
                .prenomAdmin(famille.getCreateur().getPrenom())
                .emailAdmin(famille.getCreateur().getEmail())
                .nombreMembres(membreFamilleRepository.countByFamilleId(famille.getId()))
                .build();
    }

    private QuizPublicDTO convertirQuizPublic(Quiz quiz) {
        return QuizPublicDTO.builder()
                .id(quiz.getId())
                .titre(quiz.getTitre())
                .description(quiz.getDescription())
                .typeQuiz(quiz.getTypeQuiz())
                .statut(quiz.getStatut())
                .dateCreation(quiz.getDateCreation())
                .nomCreateur(quiz.getCreateur().getNom())
                .prenomCreateur(quiz.getCreateur().getPrenom())
                .titreContenu(quiz.getTitre()) // Utiliser le titre du quiz
                .nomFamille(quiz.getFamille() != null ? quiz.getFamille().getNom() : null) // Utiliser la famille du quiz
                .build();
    }
    /**
     * Récupère le détail d’un proverbe spécifique.
     */
    @Transactional(readOnly = true)
    public ContenuGlobalDTO getDetailProverbe(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proverbe introuvable avec l'ID " + id));

        if (!"PROVERBE".equalsIgnoreCase(contenu.getTypeContenu())) {
            throw new RuntimeException("Ce contenu n'est pas un proverbe !");
        }

        return convertirContenuGlobal(contenu);
    }

    /**
     * Récupère le détail d’un artisanat spécifique.
     */
    @Transactional(readOnly = true)
    public ContenuGlobalDTO getDetailArtisanat(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artisanat introuvable avec l'ID " + id));

        if (!"ARTISANAT".equalsIgnoreCase(contenu.getTypeContenu())) {
            throw new RuntimeException("Ce contenu n'est pas un artisanat !");
        }

        return convertirContenuGlobal(contenu);
    }

    /**
     * Récupère le détail d’une devinette spécifique.
     */
    @Transactional(readOnly = true)
    public ContenuGlobalDTO getDetailDevinette(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devinette introuvable avec l'ID " + id));

        if (!"DEVINETTE".equalsIgnoreCase(contenu.getTypeContenu())) {
            throw new RuntimeException("Ce contenu n'est pas une devinette !");
        }

        return convertirContenuGlobal(contenu);
    }


    // ⭐ MÉTHODE MODIFIÉE
    private ContenuGlobalDTO convertirContenuGlobal(Contenu contenu) {
        // Sécuriser les relations optionnelles
        String nomFamille = contenu.getFamille() != null ? contenu.getFamille().getNom() : null;
        String regionFamille = contenu.getFamille() != null ? contenu.getFamille().getRegion() : null;

        ContenuGlobalDTO.ContenuGlobalDTOBuilder builder = ContenuGlobalDTO.builder()
                .id(contenu.getId())
                .titre(contenu.getTitre())
                .description(contenu.getDescription())
                .typeContenu(contenu.getTypeContenu())
                .statut(contenu.getStatut())
                .dateCreation(contenu.getDateCreation())
                .nomCreateur(contenu.getAuteur().getNom())
                .prenomCreateur(contenu.getAuteur().getPrenom())
                .emailCreateur(contenu.getAuteur().getEmail())
                .nomFamille(nomFamille)
                .regionFamille(regionFamille)
                .thumbnailUrl(contenu.getUrlPhoto()); // Miniature

        // ✅ Ajouter le mappage spécifique si le contenu est un PROVERBE
        if ("PROVERBE".equalsIgnoreCase(contenu.getTypeContenu())) {
            builder
                    .texteProverbe(contenu.getTexteProverbe())
                    .significationProverbe(contenu.getSignificationProverbe())
                    .origineProverbe(contenu.getOrigineProverbe())
                    .photoProverbe("http://localhost:8080/" + contenu.getUrlPhoto());
        }

        return builder.build();
    }
}