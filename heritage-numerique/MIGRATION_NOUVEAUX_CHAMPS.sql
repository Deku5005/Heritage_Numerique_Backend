-- =====================================================
-- MIGRATION SQL POUR AJOUTER LES NOUVEAUX CHAMPS
-- Heritage Numérique - Ajout numero_telephone, ethnie, region
-- =====================================================

-- Ajouter les nouveaux champs à la table utilisateur
ALTER TABLE utilisateur 
ADD COLUMN numero_telephone VARCHAR(20) COMMENT 'Numéro de téléphone de l\'utilisateur',
ADD COLUMN ethnie VARCHAR(100) COMMENT 'Ethnie de l\'utilisateur';

-- Ajouter un index sur ethnie pour les recherches
ALTER TABLE utilisateur 
ADD INDEX idx_ethnie (ethnie);

-- Ajouter le nouveau champ à la table famille
ALTER TABLE famille 
ADD COLUMN ethnie VARCHAR(100) COMMENT 'Ethnie de la famille';

-- Ajouter un index sur ethnie pour les recherches
ALTER TABLE famille 
ADD INDEX idx_ethnie (ethnie);

-- Ajouter le nouveau champ à la table contenu
ALTER TABLE contenu 
ADD COLUMN region VARCHAR(100) COMMENT 'Région de l\'événement';

-- Ajouter un index sur region pour les recherches
ALTER TABLE contenu 
ADD INDEX idx_region (region);

-- =====================================================
-- VÉRIFICATION DES CHAMPS AJOUTÉS
-- =====================================================

-- Vérifier la structure de la table utilisateur
DESCRIBE utilisateur;

-- Vérifier la structure de la table famille
DESCRIBE famille;

-- Vérifier la structure de la table contenu
DESCRIBE contenu;

-- Vérifier les index
SHOW INDEX FROM utilisateur;
SHOW INDEX FROM famille;
SHOW INDEX FROM contenu;
