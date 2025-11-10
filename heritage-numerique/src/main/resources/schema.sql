-- =====================================================
-- SCHEMA SQL MYSQL POUR HERITAGE NUMERIQUE
-- Base de données pour gestion de patrimoine familial
-- Engine: InnoDB, Charset: utf8mb4
-- =====================================================

-- Suppression des tables existantes (ordre inversé des dépendances)
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS membre_arbre;
DROP TABLE IF EXISTS arbre_genealogique;
DROP TABLE IF EXISTS resultat_quiz;
DROP TABLE IF EXISTS proposition;
DROP TABLE IF EXISTS question_quiz;
DROP TABLE IF EXISTS quiz;
DROP TABLE IF EXISTS demande_publication;
DROP TABLE IF EXISTS traduction_contenu;
DROP TABLE IF EXISTS contenu;
DROP TABLE IF EXISTS categorie;
DROP TABLE IF EXISTS invitation;
DROP TABLE IF EXISTS membre_famille;
DROP TABLE IF EXISTS famille;
DROP TABLE IF EXISTS utilisateur;

-- =====================================================
-- TABLE: utilisateur
-- Stocke les informations des utilisateurs de l'application
-- =====================================================
CREATE TABLE utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    numero_telephone VARCHAR(20) COMMENT 'Numéro de téléphone de l\'utilisateur',
    ethnie VARCHAR(100) COMMENT 'Ethnie de l\'utilisateur',
    mot_de_passe VARCHAR(255) NOT NULL COMMENT 'Hash BCrypt du mot de passe',
    role ENUM('ROLE_ADMIN', 'ROLE_MEMBRE') NOT NULL DEFAULT 'ROLE_MEMBRE',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_ethnie (ethnie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des utilisateurs avec authentification';

-- =====================================================
-- TABLE: famille
-- Représente une famille/groupe familial
-- =====================================================
CREATE TABLE famille (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    ethnie VARCHAR(100) COMMENT 'Ethnie de la famille',
    region VARCHAR(100) COMMENT 'Région de la famille',
    id_createur BIGINT NOT NULL COMMENT 'Utilisateur qui a créé la famille',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_createur) REFERENCES utilisateur(id) ON DELETE RESTRICT,
    INDEX idx_createur (id_createur),
    INDEX idx_ethnie (ethnie),
    INDEX idx_region (region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table des familles';

-- =====================================================
-- TABLE: membre_famille
-- Lien entre utilisateurs et familles (relation N:N)
-- =====================================================
CREATE TABLE membre_famille (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur BIGINT NOT NULL,
    id_famille BIGINT NOT NULL,
    role_famille ENUM('ADMIN', 'EDITEUR', 'LECTEUR') NOT NULL DEFAULT 'LECTEUR',
    lien_parente VARCHAR(50) COMMENT 'Lien de parenté au sein de la famille',
    date_ajout DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (id_famille) REFERENCES famille(id) ON DELETE CASCADE,
    UNIQUE KEY unique_membre_famille (id_utilisateur, id_famille),
    INDEX idx_famille (id_famille),
    INDEX idx_utilisateur (id_utilisateur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Association utilisateur-famille avec rôle spécifique';

-- =====================================================
-- TABLE: invitation
-- Invitations pour rejoindre une famille
-- =====================================================
CREATE TABLE invitation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_famille BIGINT NOT NULL,
    id_emetteur BIGINT NOT NULL COMMENT 'Utilisateur qui envoie l\'invitation',
    id_utilisateur_invite BIGINT COMMENT 'Utilisateur destinataire (rempli après inscription)',
    nom_invite VARCHAR(100) COMMENT 'Nom de la personne invitée',
    email_invite VARCHAR(255) NOT NULL COMMENT 'Email de la personne invitée',
    telephone_invite VARCHAR(20) COMMENT 'Numéro de téléphone de la personne invitée',
    lien_parente VARCHAR(50) COMMENT 'Lien de parenté avec la famille',
    code_invitation VARCHAR(8) NOT NULL UNIQUE COMMENT 'Code alphanumérique unique',
    statut ENUM('EN_ATTENTE', 'ACCEPTEE', 'REFUSEE', 'EXPIREE') NOT NULL DEFAULT 'EN_ATTENTE',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_expiration DATETIME NOT NULL COMMENT 'Expire après 48 heures',
    date_utilisation DATETIME NULL COMMENT 'Date d\'acceptation/refus',
    FOREIGN KEY (id_famille) REFERENCES famille(id) ON DELETE CASCADE,
    FOREIGN KEY (id_emetteur) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (id_utilisateur_invite) REFERENCES utilisateur(id) ON DELETE SET NULL,
    INDEX idx_code_invitation (code_invitation),
    INDEX idx_email_invite (email_invite),
    INDEX idx_statut (statut),
    INDEX idx_famille (id_famille),
    INDEX idx_utilisateur_invite (id_utilisateur_invite)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Invitations pour rejoindre une famille';

-- =====================================================
-- TABLE: categorie
-- Catégories de contenus (photos, vidéos, documents, etc.)
-- =====================================================
CREATE TABLE categorie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icone VARCHAR(50) COMMENT 'Nom de l\'icône UI',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Catégories de contenus';

-- =====================================================
-- TABLE: contenu
-- Contenus partagés dans une famille (photos, vidéos, textes, etc.)
-- =====================================================
CREATE TABLE contenu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_famille BIGINT NOT NULL,
    id_auteur BIGINT NOT NULL COMMENT 'Utilisateur qui a créé le contenu',
    id_categorie BIGINT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type_contenu ENUM('CONTE', 'ARTISANAT', 'PROVERBE', 'DEVINETTE', 'PHOTO', 'VIDEO', 'AUDIO', 'DOCUMENT', 'TEXTE') NOT NULL,
    url_fichier VARCHAR(500) COMMENT 'URL du fichier (S3, local, etc.)',
    url_photo VARCHAR(500) COMMENT 'URL de la photo associée',
    taille_fichier BIGINT COMMENT 'Taille en octets',
    duree INT COMMENT 'Durée en secondes (pour audio/vidéo)',
    date_evenement DATE COMMENT 'Date de l\'événement représenté',
    lieu VARCHAR(255) COMMENT 'Lieu de l\'événement',
    region VARCHAR(100) COMMENT 'Région de l\'événement',
    statut ENUM('BROUILLON', 'PUBLIE', 'ARCHIVE') NOT NULL DEFAULT 'BROUILLON',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_famille) REFERENCES famille(id) ON DELETE CASCADE,
    FOREIGN KEY (id_auteur) REFERENCES utilisateur(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_categorie) REFERENCES categorie(id) ON DELETE RESTRICT,
    INDEX idx_famille (id_famille),
    INDEX idx_auteur (id_auteur),
    INDEX idx_categorie (id_categorie),
    INDEX idx_statut (statut),
    INDEX idx_date_evenement (date_evenement),
    INDEX idx_region (region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Contenus multimédias partagés';

-- =====================================================
-- TABLE: traduction_contenu
-- Traductions multilingues des contenus
-- =====================================================
CREATE TABLE traduction_contenu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_contenu BIGINT NOT NULL,
    langue ENUM('FR', 'EN', 'BM') NOT NULL COMMENT 'Langues supportées : FR (Français), EN (Anglais), BM (Bambara)',
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_contenu) REFERENCES contenu(id) ON DELETE CASCADE,
    UNIQUE KEY unique_contenu_langue (id_contenu, langue),
    INDEX idx_contenu (id_contenu),
    INDEX idx_langue (langue)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Traductions multilingues des contenus (Français, Anglais, Bambara)';

-- =====================================================
-- TABLE: demande_publication
-- Demandes de publication de contenus (workflow de validation)
-- =====================================================
CREATE TABLE demande_publication (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_contenu BIGINT NOT NULL,
    id_demandeur BIGINT NOT NULL COMMENT 'Utilisateur qui demande la publication',
    id_valideur BIGINT COMMENT 'Utilisateur qui valide/rejette',
    statut ENUM('EN_ATTENTE', 'APPROUVEE', 'REJETEE') NOT NULL DEFAULT 'EN_ATTENTE',
    commentaire TEXT COMMENT 'Raison du rejet ou commentaire',
    date_demande DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_traitement DATETIME COMMENT 'Date de validation/rejet',
    FOREIGN KEY (id_contenu) REFERENCES contenu(id) ON DELETE CASCADE,
    FOREIGN KEY (id_demandeur) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (id_valideur) REFERENCES utilisateur(id) ON DELETE SET NULL,
    INDEX idx_contenu (id_contenu),
    INDEX idx_demandeur (id_demandeur),
    INDEX idx_statut (statut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Workflow de validation des publications';

-- =====================================================
-- TABLE: quiz
-- Quiz sur l'histoire familiale
-- =====================================================
CREATE TABLE quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_famille BIGINT NOT NULL,
    id_contenu BIGINT COMMENT 'Contenu associé au quiz (optionnel)',
    id_createur BIGINT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    difficulte ENUM('FACILE', 'MOYEN', 'DIFFICILE') NOT NULL DEFAULT 'MOYEN',
    temps_limite INT COMMENT 'Temps limite en secondes (optionnel)',
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_famille) REFERENCES famille(id) ON DELETE CASCADE,
    FOREIGN KEY (id_contenu) REFERENCES contenu(id) ON DELETE CASCADE,
    FOREIGN KEY (id_createur) REFERENCES utilisateur(id) ON DELETE RESTRICT,
    INDEX idx_famille (id_famille),
    INDEX idx_contenu (id_contenu),
    INDEX idx_createur (id_createur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Quiz sur l\'histoire familiale';

-- =====================================================
-- TABLE: question_quiz
-- Questions d'un quiz
-- =====================================================
CREATE TABLE question_quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_quiz BIGINT NOT NULL,
    texte_question TEXT NOT NULL,
    type_question ENUM('QCM', 'VRAI_FAUX', 'TEXTE_LIBRE') NOT NULL DEFAULT 'QCM',
    ordre INT NOT NULL COMMENT 'Ordre d\'affichage de la question',
    points INT NOT NULL DEFAULT 1,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_quiz) REFERENCES quiz(id) ON DELETE CASCADE,
    INDEX idx_quiz (id_quiz),
    INDEX idx_ordre (ordre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Questions des quiz';

-- =====================================================
-- TABLE: proposition
-- Propositions de réponse pour les questions de quiz
-- =====================================================
CREATE TABLE proposition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_question BIGINT NOT NULL,
    texte_proposition TEXT NOT NULL,
    est_correcte BOOLEAN NOT NULL DEFAULT FALSE,
    ordre INT NOT NULL COMMENT 'Ordre d\'affichage de la proposition',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_question) REFERENCES question_quiz(id) ON DELETE CASCADE,
    INDEX idx_question (id_question)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Propositions de réponse pour les questions';

-- =====================================================
-- TABLE: resultat_quiz
-- Résultats des quiz passés par les utilisateurs
-- =====================================================
CREATE TABLE resultat_quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_quiz BIGINT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    score INT NOT NULL COMMENT 'Score obtenu',
    score_max INT NOT NULL COMMENT 'Score maximum possible',
    temps_ecoule INT COMMENT 'Temps écoulé en secondes',
    date_passage DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_quiz) REFERENCES quiz(id) ON DELETE CASCADE,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id) ON DELETE CASCADE,
    INDEX idx_quiz (id_quiz),
    INDEX idx_utilisateur (id_utilisateur),
    INDEX idx_date_passage (date_passage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Résultats des quiz';

-- =====================================================
-- TABLE: arbre_genealogique
-- Arbres généalogiques des familles
-- =====================================================
CREATE TABLE arbre_genealogique (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_famille BIGINT NOT NULL,
    nom VARCHAR(200) NOT NULL,
    description TEXT,
    id_createur BIGINT NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_famille) REFERENCES famille(id) ON DELETE CASCADE,
    FOREIGN KEY (id_createur) REFERENCES utilisateur(id) ON DELETE RESTRICT,
    INDEX idx_famille (id_famille)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Arbres généalogiques';

-- =====================================================
-- TABLE: membre_arbre
-- Membres (personnes) dans un arbre généalogique
-- =====================================================
CREATE TABLE membre_arbre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_arbre BIGINT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    sexe ENUM('M', 'F', 'AUTRE') COMMENT 'Sexe du membre',
    date_naissance DATE,
    date_deces DATE,
    lieu_naissance VARCHAR(255),
    lieu_deces VARCHAR(255),
    id_pere BIGINT COMMENT 'Référence au père dans l\'arbre',
    id_mere BIGINT COMMENT 'Référence à la mère dans l\'arbre',
    id_utilisateur_lie BIGINT COMMENT 'Lien vers un utilisateur réel (optionnel)',
    biographie TEXT,
    photo_url VARCHAR(500),
    relation_familiale VARCHAR(100) COMMENT 'Relation familiale (père, mère, fils, fille, etc.)',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_arbre) REFERENCES arbre_genealogique(id) ON DELETE CASCADE,
    FOREIGN KEY (id_pere) REFERENCES membre_arbre(id) ON DELETE SET NULL,
    FOREIGN KEY (id_mere) REFERENCES membre_arbre(id) ON DELETE SET NULL,
    FOREIGN KEY (id_utilisateur_lie) REFERENCES utilisateur(id) ON DELETE SET NULL,
    INDEX idx_arbre (id_arbre),
    INDEX idx_pere (id_pere),
    INDEX idx_mere (id_mere),
    INDEX idx_utilisateur_lie (id_utilisateur_lie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Membres d\'un arbre généalogique';

-- =====================================================
-- TABLE: notification
-- Notifications envoyées aux utilisateurs
-- =====================================================
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_destinataire BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'Type de notification (INVITATION, ACCEPTATION, etc.)',
    titre VARCHAR(255) NOT NULL,
    message TEXT,
    canal VARCHAR(20) NOT NULL COMMENT 'Canal d\'envoi (EMAIL, SMS, IN_APP)',
    lu BOOLEAN NOT NULL DEFAULT FALSE,
    date_envoi DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_lecture DATETIME COMMENT 'Date de lecture',
    lien VARCHAR(500) COMMENT 'Lien vers la ressource concernée',
    metadata TEXT COMMENT 'Métadonnées JSON supplémentaires',
    FOREIGN KEY (id_destinataire) REFERENCES utilisateur(id) ON DELETE CASCADE,
    INDEX idx_destinataire (id_destinataire),
    INDEX idx_type (type),
    INDEX idx_lu (lu),
    INDEX idx_date_envoi (date_envoi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Notifications envoyées aux utilisateurs';

-- =====================================================
-- INSERTION DE DONNÉES INITIALES
-- =====================================================

-- Catégories par défaut (mise à jour selon les besoins du projet)
INSERT INTO categorie (nom, description, icone) VALUES
('Contes', 'Contes familiaux', 'book'),
('Artisanats', 'Artisanats familiaux', 'palette'),
('Devinettes', 'Devinettes familiales', 'question'),
('Proverbes', 'Proverbes familiaux', 'quote');

-- =====================================================
-- FIN DU SCHEMA
-- =====================================================

