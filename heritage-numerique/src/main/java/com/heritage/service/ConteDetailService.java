package com.heritage.service;

import com.heritage.dto.ConteDetailDTO;
import com.heritage.entite.Contenu;
import com.heritage.entite.MembreFamille;
import com.heritage.exception.NotFoundException;
import com.heritage.repository.ContenuRepository;
import com.heritage.repository.MembreFamilleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des contes avec leur contenu détaillé.
 * Permet d'extraire et d'afficher le contenu des fichiers PDF et texte.
 */
@Service
public class ConteDetailService {

    private final ContenuRepository contenuRepository;
    private final MembreFamilleRepository membreFamilleRepository;
    private final FileContentService fileContentService;

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    public ConteDetailService(ContenuRepository contenuRepository,
                             MembreFamilleRepository membreFamilleRepository,
                             FileContentService fileContentService) {
        this.contenuRepository = contenuRepository;
        this.membreFamilleRepository = membreFamilleRepository;
        this.fileContentService = fileContentService;
    }

    /**
     * Récupère un conte avec son contenu détaillé.
     * 
     * @param conteId ID du conte
     * @return DTO du conte avec son contenu
     */
    @Transactional(readOnly = true)
    public ConteDetailDTO getConteDetail(Long conteId) {
        // 1. Récupérer le conte
        Contenu conte = contenuRepository.findById(conteId)
                .orElseThrow(() -> new NotFoundException("Conte non trouvé"));

        if (!"CONTE".equals(conte.getTypeContenu())) {
            throw new NotFoundException("Ce contenu n'est pas un conte");
        }

        // 2. Récupérer les informations de l'auteur
        MembreFamille membreAuteur = membreFamilleRepository
                .findByUtilisateurIdAndFamilleId(conte.getAuteur().getId(), conte.getFamille().getId())
                .orElse(null);

        String roleAuteur = "INCONNU";
        String lienParenteAuteur = "Non spécifié";
        
        if (membreAuteur != null) {
            roleAuteur = membreAuteur.getRoleFamille().toString();
            // Note: Le champ lienParente n'existe pas dans l'entité MembreFamille
            lienParenteAuteur = "Non spécifié";
        }

        // 3. Extraire le contenu du fichier si disponible
        String contenuTexte = "";
        String typeFichier = "";
        Integer nombreMots = 0;
        Integer nombreCaracteres = 0;
        Integer nombreLignes = 0;

        if (conte.getUrlFichier() != null && !conte.getUrlFichier().isEmpty()) {
            try {
                // Charger le fichier
                Path filePath = Paths.get(uploadPath, conte.getUrlFichier());
                Resource resource = new UrlResource(filePath.toUri());
                
                if (resource.exists() && resource.isReadable()) {
                    // Créer un MultipartFile temporaire pour l'extraction
                    MultipartFile fichier = new MultipartFile() {
                        @Override
                        public String getName() {
                            return conte.getUrlFichier();
                        }

                        @Override
                        public String getOriginalFilename() {
                            return conte.getUrlFichier();
                        }

                        @Override
                        public String getContentType() {
                            return conte.getUrlFichier().endsWith(".pdf") ? "application/pdf" : "text/plain";
                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }

                        @Override
                        public long getSize() {
                            try {
                                return resource.contentLength();
                            } catch (IOException e) {
                                return 0;
                            }
                        }

                        @Override
                        public byte[] getBytes() throws IOException {
                            return resource.getInputStream().readAllBytes();
                        }

                        @Override
                        public java.io.InputStream getInputStream() throws IOException {
                            return resource.getInputStream();
                        }

                        @Override
                        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                            // Non implémenté pour ce cas d'usage
                        }
                    };

                    // Extraire le texte
                    contenuTexte = fileContentService.lireContenuFichier(conte.getUrlFichier());
                    
                    // Déterminer le type de fichier
                    String nomFichier = conte.getUrlFichier();
                    if (nomFichier.endsWith(".pdf")) {
                        typeFichier = "PDF";
                    } else if (nomFichier.endsWith(".txt")) {
                        typeFichier = "TXT";
                    } else if (nomFichier.endsWith(".doc")) {
                        typeFichier = "DOC";
                    } else if (nomFichier.endsWith(".docx")) {
                        typeFichier = "DOCX";
                    } else {
                        typeFichier = "AUTRE";
                    }

                    // Calculer les statistiques du texte
                    nombreMots = compterMots(contenuTexte);
                    nombreCaracteres = contenuTexte.length();
                    nombreLignes = compterLignes(contenuTexte);
                }
            } catch (Exception e) {
                // En cas d'erreur, on continue sans le contenu
                contenuTexte = "Erreur lors de l'extraction du contenu: " + e.getMessage();
            }
        }

        return ConteDetailDTO.builder()
                .id(conte.getId())
                .titre(conte.getTitre())
                .description(conte.getDescription())
                .nomAuteur(conte.getAuteur().getNom())
                .prenomAuteur(conte.getAuteur().getPrenom())
                .emailAuteur(conte.getAuteur().getEmail())
                .roleAuteur(roleAuteur)
                .lienParenteAuteur(lienParenteAuteur)
                .dateCreation(conte.getDateCreation())
                .statut(conte.getStatut())
                .urlFichier(conte.getUrlFichier())
                .urlPhoto(conte.getUrlFichier())
                .lieu(conte.getLieu())
                .region(conte.getRegion())
                .idFamille(conte.getFamille().getId())
                .nomFamille(conte.getFamille().getNom())
                .contenuTexte(contenuTexte)
                .typeFichier(typeFichier)
                .nombreMots(nombreMots)
                .nombreCaracteres(nombreCaracteres)
                .nombreLignes(nombreLignes)
                .build();
    }

    /**
     * Récupère tous les contes d'une famille avec leur contenu détaillé.
     * 
     * @param familleId ID de la famille
     * @return Liste des contes avec leur contenu
     */
    @Transactional(readOnly = true)
    public List<ConteDetailDTO> getContesDetailByFamille(Long familleId) {
        List<Contenu> contes = contenuRepository.findByFamilleIdAndTypeContenu(familleId, "CONTE");
        
        return contes.stream()
                .map(conte -> {
                    try {
                        return getConteDetail(conte.getId());
                    } catch (Exception e) {
                        // En cas d'erreur, retourner un DTO basique
                        return ConteDetailDTO.builder()
                                .id(conte.getId())
                                .titre(conte.getTitre())
                                .description(conte.getDescription())
                                .nomAuteur(conte.getAuteur().getNom())
                                .prenomAuteur(conte.getAuteur().getPrenom())
                                .emailAuteur(conte.getAuteur().getEmail())
                                .dateCreation(conte.getDateCreation())
                                .statut(conte.getStatut())
                                .urlFichier(conte.getUrlFichier())
                                .urlPhoto(conte.getUrlFichier())
                                .lieu(conte.getLieu())
                                .region(conte.getRegion())
                                .idFamille(conte.getFamille().getId())
                                .nomFamille(conte.getFamille().getNom())
                                .contenuTexte("Erreur lors du chargement du contenu")
                                .typeFichier("ERREUR")
                                .nombreMots(0)
                                .nombreCaracteres(0)
                                .nombreLignes(0)
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Compte le nombre de mots dans un texte.
     */
    private Integer compterMots(String texte) {
        if (texte == null || texte.trim().isEmpty()) {
            return 0;
        }
        return texte.trim().split("\\s+").length;
    }

    /**
     * Compte le nombre de lignes dans un texte.
     */
    private Integer compterLignes(String texte) {
        if (texte == null || texte.isEmpty()) {
            return 0;
        }
        return texte.split("\n").length;
    }
}
