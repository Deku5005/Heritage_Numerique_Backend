-- Script de correction de la base de données
USE heritage_numerique;

-- Vérifier la structure de la table utilisateur
DESCRIBE utilisateur;

-- Si les colonnes n'existent pas, les ajouter
ALTER TABLE utilisateur 
ADD COLUMN IF NOT EXISTS numero_telephone VARCHAR(20) COMMENT 'Numéro de téléphone de l\'utilisateur',
ADD COLUMN IF NOT EXISTS ethnie VARCHAR(100) COMMENT 'Ethnie de l\'utilisateur';

-- Vérifier la structure de la table famille
DESCRIBE famille;

-- Ajouter la colonne ethnie à la table famille si elle n'existe pas
ALTER TABLE famille 
ADD COLUMN IF NOT EXISTS ethnie VARCHAR(100) COMMENT 'Ethnie de la famille';

-- Vérifier la structure de la table contenu
DESCRIBE contenu;

-- Ajouter la colonne region à la table contenu si elle n'existe pas
ALTER TABLE contenu 
ADD COLUMN IF NOT EXISTS region VARCHAR(100) COMMENT 'Région de l\'événement';

-- Vérifier que toutes les tables existent
SHOW TABLES;

-- Afficher la structure finale de la table utilisateur
DESCRIBE utilisateur;
