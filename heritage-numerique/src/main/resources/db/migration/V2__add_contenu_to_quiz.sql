-- Migration : Ajout du champ id_contenu à la table quiz
-- Date : 2025-11-10
-- Description : Permet de lier directement un quiz à un contenu (conte)

-- Ajouter la colonne id_contenu
ALTER TABLE quiz 
ADD COLUMN id_contenu BIGINT COMMENT 'Contenu associé au quiz (optionnel)' AFTER id_famille;

-- Ajouter la contrainte de clé étrangère
ALTER TABLE quiz
ADD CONSTRAINT fk_quiz_contenu
FOREIGN KEY (id_contenu) REFERENCES contenu(id) ON DELETE CASCADE;

-- Ajouter l'index pour améliorer les performances
ALTER TABLE quiz
ADD INDEX idx_contenu (id_contenu);

