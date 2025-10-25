-- Test de création des tables avec les nouveaux champs
USE heritage_numerique;

-- Supprimer les tables existantes
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

-- Créer la table utilisateur avec les nouveaux champs
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

-- Insérer un utilisateur de test
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, numero_telephone, ethnie) 
VALUES ('Test', 'User', 'test@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqjO3VtVK0LqPZxR3qJxJxJxJ', '+223 70 12 34 56', 'Bambara');

-- Vérifier que la table a été créée
SHOW TABLES;

-- Vérifier la structure de la table utilisateur
DESCRIBE utilisateur;

-- Vérifier les données insérées
SELECT * FROM utilisateur;
