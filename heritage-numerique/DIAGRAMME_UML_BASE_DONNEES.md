# 🗄️ DIAGRAMME UML - BASE DE DONNÉES HERITAGE NUMERIQUE

## 📊 Vue d'ensemble du système

Ce document décrit l'architecture complète de la base de données Heritage Numérique avec toutes les tables, leurs champs et leurs relations.

---

## 📋 TABLE DES MATIÈRES

1. [Utilisateur](#1-utilisateur)
2. [Famille](#2-famille)
3. [MembreFamille](#3-membrefamille)
4. [Invitation](#4-invitation)
5. [Categorie](#5-categorie)
6. [Contenu](#6-contenu)
7. [TraductionContenu](#7-traductioncontenu)
8. [DemandePublication](#8-demandepublication)
9. [Quiz](#9-quiz)
10. [QuestionQuiz](#10-questionquiz)
11. [Proposition](#11-proposition)
12. [ResultatQuiz](#12-resultatquiz)
13. [ArbreGenealogique](#13-arbregene alogique)
14. [MembreArbre](#14-membrearbre)
15. [Notification](#15-notification)
16. [Diagramme des Relations](#diagramme-des-relations)

---

## 1. 👤 UTILISATEUR

**Description** : Représente un utilisateur de l'application avec ses informations d'authentification.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique de l'utilisateur |
| `nom` | VARCHAR(100) | Nom de famille de l'utilisateur |
| `prenom` | VARCHAR(100) | Prénom de l'utilisateur |
| `email` | VARCHAR(255) UNIQUE | Adresse email (utilisée pour l'authentification) |
| `numero_telephone` | VARCHAR(20) | Numéro de téléphone (optionnel) |
| `ethnie` | VARCHAR(100) | Ethnie de l'utilisateur (optionnel) |
| `mot_de_passe` | VARCHAR(255) | Hash BCrypt du mot de passe |
| `role` | ENUM | Rôle global : `ROLE_ADMIN` (super-admin) ou `ROLE_MEMBRE` |
| `date_creation` | DATETIME | Date de création du compte |
| `date_modification` | DATETIME | Date de dernière modification |
| `actif` | BOOLEAN | Indique si le compte est actif |

### Relations sortantes
- **1 → N** : `Famille` (créateur) - Un utilisateur peut créer plusieurs familles
- **1 → N** : `MembreFamille` - Un utilisateur peut appartenir à plusieurs familles
- **1 → N** : `Invitation` (émetteur) - Un utilisateur peut envoyer plusieurs invitations
- **1 → N** : `Contenu` (auteur) - Un utilisateur peut créer plusieurs contenus
- **1 → N** : `Quiz` (créateur) - Un utilisateur peut créer plusieurs quiz
- **1 → N** : `ResultatQuiz` - Un utilisateur peut passer plusieurs quiz
- **1 → N** : `ArbreGenealogique` (créateur) - Un utilisateur peut créer plusieurs arbres
- **1 → N** : `Notification` - Un utilisateur peut recevoir plusieurs notifications
- **1 → N** : `DemandePublication` (demandeur/valideur) - Un utilisateur peut faire/valider plusieurs demandes

---

## 2. 👨‍👩‍👧‍👦 FAMILLE

**Description** : Représente un groupe familial qui peut partager des contenus et des quiz.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique de la famille |
| `nom` | VARCHAR(200) | Nom de la famille |
| `description` | TEXT | Description de la famille (optionnel) |
| `ethnie` | VARCHAR(100) | Ethnie de la famille (optionnel) |
| `region` | VARCHAR(100) | Région géographique de la famille (optionnel) |
| `id_createur` | BIGINT (FK) | Référence vers l'utilisateur qui a créé la famille |
| `date_creation` | DATETIME | Date de création de la famille |
| `date_modification` | DATETIME | Date de dernière modification |

### Relations

**Entrantes :**
- **N → 1** : `Utilisateur` (créateur) - Une famille est créée par un utilisateur

**Sortantes :**
- **1 → N** : `MembreFamille` - Une famille peut avoir plusieurs membres
- **1 → N** : `Invitation` - Une famille peut avoir plusieurs invitations
- **1 → N** : `Contenu` - Une famille peut avoir plusieurs contenus
- **1 → N** : `Quiz` - Une famille peut avoir plusieurs quiz
- **1 → N** : `ArbreGenealogique` - Une famille peut avoir plusieurs arbres généalogiques

---

## 3. 🔗 MEMBREFAMILLE

**Description** : Table d'association entre utilisateurs et familles (relation N-N) avec un rôle spécifique.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_utilisateur` | BIGINT (FK) | Référence vers l'utilisateur |
| `id_famille` | BIGINT (FK) | Référence vers la famille |
| `role_famille` | ENUM | Rôle dans la famille : `ADMIN`, `EDITEUR`, `LECTEUR` |
| `lien_parente` | VARCHAR(50) | Lien de parenté (ex: "Père", "Mère", "Fils", "Fille") |
| `date_ajout` | DATETIME | Date d'ajout à la famille |

### Relations

**Entrantes :**
- **N → 1** : `Utilisateur` - Un membre appartient à un utilisateur
- **N → 1** : `Famille` - Un membre appartient à une famille

**Contrainte :** Unique sur (`id_utilisateur`, `id_famille`) - Un utilisateur ne peut être membre qu'une seule fois par famille

### Rôles dans la famille

- **ADMIN** : Peut gérer la famille, inviter des membres, créer du contenu, gérer les quiz
- **EDITEUR** : Peut créer du contenu et des quiz
- **LECTEUR** : Peut seulement consulter les contenus de la famille

---

## 4. 📧 INVITATION

**Description** : Invitations pour rejoindre une famille avec un code unique.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_famille` | BIGINT (FK) | Référence vers la famille |
| `id_emetteur` | BIGINT (FK) | Utilisateur qui envoie l'invitation |
| `id_utilisateur_invite` | BIGINT (FK) | Utilisateur invité (rempli après acceptation) |
| `nom_invite` | VARCHAR(100) | Nom de la personne invitée |
| `email_invite` | VARCHAR(255) | Email de la personne invitée |
| `telephone_invite` | VARCHAR(20) | Téléphone de la personne invitée (optionnel) |
| `lien_parente` | VARCHAR(50) | Lien de parenté suggéré |
| `code_invitation` | VARCHAR(8) UNIQUE | Code alphanumérique unique (8 caractères) |
| `statut` | ENUM | Statut : `EN_ATTENTE`, `ACCEPTEE`, `REFUSEE`, `EXPIREE` |
| `date_creation` | DATETIME | Date de création de l'invitation |
| `date_expiration` | DATETIME | Date d'expiration (48 heures après création) |
| `date_utilisation` | DATETIME | Date d'acceptation/refus |

### Relations

**Entrantes :**
- **N → 1** : `Famille` - Une invitation est pour une famille
- **N → 1** : `Utilisateur` (émetteur) - Une invitation est envoyée par un utilisateur
- **N → 1** : `Utilisateur` (invité) - Une invitation peut être acceptée par un utilisateur

---

## 5. 🏷️ CATEGORIE

**Description** : Catégories de contenus (Contes, Artisanats, Proverbes, Devinettes, etc.).

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `nom` | VARCHAR(100) UNIQUE | Nom de la catégorie |
| `description` | TEXT | Description de la catégorie |
| `icone` | VARCHAR(50) | Nom de l'icône UI (ex: "book", "palette") |
| `date_creation` | DATETIME | Date de création |

### Relations sortantes
- **1 → N** : `Contenu` - Une catégorie peut avoir plusieurs contenus

### Catégories par défaut
- **Contes** : Contes familiaux (icône: book)
- **Artisanats** : Artisanats familiaux (icône: palette)
- **Devinettes** : Devinettes familiales (icône: question)
- **Proverbes** : Proverbes familiaux (icône: quote)

---

## 6. 📚 CONTENU

**Description** : Contenus multimédias partagés dans une famille (contes, artisanats, photos, vidéos, etc.).

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_famille` | BIGINT (FK) | Référence vers la famille |
| `id_auteur` | BIGINT (FK) | Utilisateur qui a créé le contenu |
| `id_categorie` | BIGINT (FK) | Référence vers la catégorie |
| `titre` | VARCHAR(255) | Titre du contenu |
| `description` | TEXT | Description détaillée |
| `type_contenu` | ENUM | Type : `CONTE`, `ARTISANAT`, `PROVERBE`, `DEVINETTE`, `PHOTO`, `VIDEO`, `AUDIO`, `DOCUMENT`, `TEXTE` |
| `url_fichier` | VARCHAR(500) | URL du fichier principal (PDF, vidéo, audio, etc.) |
| `url_photo` | VARCHAR(500) | URL de la photo associée/miniature |
| `taille_fichier` | BIGINT | Taille du fichier en octets |
| `duree` | INT | Durée en secondes (pour audio/vidéo) |
| `date_evenement` | DATE | Date de l'événement représenté |
| `lieu` | VARCHAR(255) | Lieu de l'événement |
| `region` | VARCHAR(100) | Région de l'événement |
| `statut` | ENUM | Statut : `BROUILLON` (privé), `PUBLIE` (public), `ARCHIVE` |
| `date_creation` | DATETIME | Date de création |
| `date_modification` | DATETIME | Date de dernière modification |

### Relations

**Entrantes :**
- **N → 1** : `Famille` - Un contenu appartient à une famille
- **N → 1** : `Utilisateur` (auteur) - Un contenu est créé par un utilisateur
- **N → 1** : `Categorie` - Un contenu appartient à une catégorie

**Sortantes :**
- **1 → N** : `TraductionContenu` - Un contenu peut avoir plusieurs traductions
- **1 → N** : `DemandePublication` - Un contenu peut avoir plusieurs demandes de publication
- **1 → N** : `Quiz` - **Un contenu peut avoir plusieurs quiz** (relation optionnelle)

### Statuts du contenu

- **BROUILLON** : Contenu privé, visible uniquement par les membres de la famille
- **PUBLIE** : Contenu public, visible par tous (après validation super-admin)
- **ARCHIVE** : Contenu archivé, non visible

---

## 7. 🌍 TRADUCTIONCONTENU

**Description** : Traductions multilingues des contenus (Français, Anglais, Bambara).

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_contenu` | BIGINT (FK) | Référence vers le contenu |
| `langue` | ENUM | Langue : `FR` (Français), `EN` (Anglais), `BM` (Bambara) |
| `titre` | VARCHAR(255) | Titre traduit |
| `description` | TEXT | Description traduite |
| `date_creation` | DATETIME | Date de création de la traduction |

### Relations

**Entrantes :**
- **N → 1** : `Contenu` - Une traduction appartient à un contenu

**Contrainte :** Unique sur (`id_contenu`, `langue`) - Un contenu ne peut avoir qu'une traduction par langue

---

## 8. 📝 DEMANDEPUBLICATION

**Description** : Workflow de validation pour publier un contenu privé en contenu public.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_contenu` | BIGINT (FK) | Référence vers le contenu |
| `id_demandeur` | BIGINT (FK) | Utilisateur qui demande la publication (ADMIN famille) |
| `id_valideur` | BIGINT (FK) | Utilisateur qui valide/rejette (SUPERADMIN) |
| `statut` | ENUM | Statut : `EN_ATTENTE`, `APPROUVEE`, `REJETEE` |
| `commentaire` | TEXT | Raison du rejet ou commentaire |
| `date_demande` | DATETIME | Date de la demande |
| `date_traitement` | DATETIME | Date de validation/rejet |

### Relations

**Entrantes :**
- **N → 1** : `Contenu` - Une demande concerne un contenu
- **N → 1** : `Utilisateur` (demandeur) - Une demande est faite par un utilisateur
- **N → 1** : `Utilisateur` (valideur) - Une demande est traitée par un super-admin

### Workflow

1. **Création** : ADMIN de famille demande la publication (`EN_ATTENTE`)
2. **Validation** : SUPERADMIN valide (`APPROUVEE`) → Contenu passe en statut `PUBLIE`
3. **Rejet** : SUPERADMIN rejette (`REJETEE`) → Contenu reste `BROUILLON`

---

## 9. 🎯 QUIZ

**Description** : Quiz sur l'histoire familiale, peut être lié à un contenu (conte).

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_famille` | BIGINT (FK) | Référence vers la famille |
| `id_contenu` | BIGINT (FK) | **Référence vers le contenu associé (optionnel)** |
| `id_createur` | BIGINT (FK) | Utilisateur qui a créé le quiz |
| `titre` | VARCHAR(255) | Titre du quiz |
| `description` | TEXT | Description du quiz |
| `difficulte` | ENUM | Difficulté : `FACILE`, `MOYEN`, `DIFFICILE` |
| `temps_limite` | INT | Temps limite en secondes (optionnel) |
| `actif` | BOOLEAN | Indique si le quiz est actif |
| `date_creation` | DATETIME | Date de création |
| `date_modification` | DATETIME | Date de dernière modification |

### Relations

**Entrantes :**
- **N → 1** : `Famille` - Un quiz appartient à une famille
- **N → 1** : `Contenu` - **Un quiz peut être lié à un contenu (optionnel)**
- **N → 1** : `Utilisateur` (créateur) - Un quiz est créé par un utilisateur

**Sortantes :**
- **1 → N** : `QuestionQuiz` - Un quiz peut avoir plusieurs questions
- **1 → N** : `ResultatQuiz` - Un quiz peut avoir plusieurs résultats

### ⚠️ Relation importante : CONTENU → QUIZ

**Cardinalité** : Un contenu peut avoir **PLUSIEURS quiz** (1 → N)
- Le champ `id_contenu` dans la table `quiz` est **optionnel** (nullable)
- Un quiz peut exister sans contenu (quiz générique de famille)
- Un quiz peut être lié à un contenu spécifique (quiz sur un conte)
- **Un même contenu peut avoir plusieurs quiz différents**

---

## 10. ❓ QUESTIONQUIZ

**Description** : Questions d'un quiz.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_quiz` | BIGINT (FK) | Référence vers le quiz |
| `texte_question` | TEXT | Texte de la question |
| `type_question` | ENUM | Type : `QCM`, `VRAI_FAUX`, `TEXTE_LIBRE` |
| `ordre` | INT | Ordre d'affichage de la question |
| `points` | INT | Nombre de points pour cette question |
| `date_creation` | DATETIME | Date de création |

### Relations

**Entrantes :**
- **N → 1** : `Quiz` - Une question appartient à un quiz

**Sortantes :**
- **1 → N** : `Proposition` - Une question peut avoir plusieurs propositions

---

## 11. ✅ PROPOSITION

**Description** : Propositions de réponse pour les questions de quiz.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_question` | BIGINT (FK) | Référence vers la question |
| `texte_proposition` | TEXT | Texte de la proposition |
| `est_correcte` | BOOLEAN | Indique si la proposition est correcte |
| `ordre` | INT | Ordre d'affichage de la proposition |
| `date_creation` | DATETIME | Date de création |

### Relations

**Entrantes :**
- **N → 1** : `QuestionQuiz` - Une proposition appartient à une question

---

## 12. 📊 RESULTATQUIZ

**Description** : Résultats des quiz passés par les utilisateurs.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_quiz` | BIGINT (FK) | Référence vers le quiz |
| `id_utilisateur` | BIGINT (FK) | Référence vers l'utilisateur |
| `score` | INT | Score obtenu |
| `score_max` | INT | Score maximum possible |
| `temps_ecoule` | INT | Temps écoulé en secondes |
| `date_passage` | DATETIME | Date de passage du quiz |

### Relations

**Entrantes :**
- **N → 1** : `Quiz` - Un résultat concerne un quiz
- **N → 1** : `Utilisateur` - Un résultat appartient à un utilisateur

---

## 13. 🌳 ARBREGENE ALOGIQUE

**Description** : Arbres généalogiques des familles.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_famille` | BIGINT (FK) | Référence vers la famille |
| `nom` | VARCHAR(200) | Nom de l'arbre généalogique |
| `description` | TEXT | Description de l'arbre |
| `id_createur` | BIGINT (FK) | Utilisateur qui a créé l'arbre |
| `date_creation` | DATETIME | Date de création |
| `date_modification` | DATETIME | Date de dernière modification |

### Relations

**Entrantes :**
- **N → 1** : `Famille` - Un arbre appartient à une famille
- **N → 1** : `Utilisateur` (créateur) - Un arbre est créé par un utilisateur

**Sortantes :**
- **1 → N** : `MembreArbre` - Un arbre peut avoir plusieurs membres

---

## 14. 👥 MEMBREARBRE

**Description** : Membres (personnes) dans un arbre généalogique avec leurs relations familiales.

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_arbre` | BIGINT (FK) | Référence vers l'arbre généalogique |
| `nom` | VARCHAR(100) | Nom de famille |
| `prenom` | VARCHAR(100) | Prénom |
| `sexe` | ENUM | Sexe : `M` (Masculin), `F` (Féminin), `AUTRE` |
| `date_naissance` | DATE | Date de naissance |
| `date_deces` | DATE | Date de décès (optionnel) |
| `lieu_naissance` | VARCHAR(255) | Lieu de naissance |
| `lieu_deces` | VARCHAR(255) | Lieu de décès |
| `id_pere` | BIGINT (FK) | Référence vers le père dans l'arbre |
| `id_mere` | BIGINT (FK) | Référence vers la mère dans l'arbre |
| `id_utilisateur_lie` | BIGINT (FK) | Lien vers un utilisateur réel (optionnel) |
| `biographie` | TEXT | Biographie de la personne |
| `photo_url` | VARCHAR(500) | URL de la photo |
| `relation_familiale` | VARCHAR(100) | Relation familiale (père, mère, fils, fille, etc.) |
| `date_creation` | DATETIME | Date de création |
| `date_modification` | DATETIME | Date de dernière modification |

### Relations

**Entrantes :**
- **N → 1** : `ArbreGenealogique` - Un membre appartient à un arbre
- **N → 1** : `MembreArbre` (père) - Un membre peut avoir un père (auto-référence)
- **N → 1** : `MembreArbre` (mère) - Un membre peut avoir une mère (auto-référence)
- **N → 1** : `Utilisateur` (lié) - Un membre peut être lié à un utilisateur réel

**Sortantes :**
- **1 → N** : `MembreArbre` (enfants via père) - Un membre peut être le père de plusieurs enfants
- **1 → N** : `MembreArbre` (enfants via mère) - Un membre peut être la mère de plusieurs enfants

---

## 15. 🔔 NOTIFICATION

**Description** : Notifications envoyées aux utilisateurs (email, SMS, in-app).

### Champs

| Nom du champ | Type | Description |
|--------------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique |
| `id_destinataire` | BIGINT (FK) | Référence vers l'utilisateur destinataire |
| `type` | VARCHAR(50) | Type de notification (INVITATION, ACCEPTATION, VALIDATION, etc.) |
| `titre` | VARCHAR(255) | Titre de la notification |
| `message` | TEXT | Message de la notification |
| `canal` | VARCHAR(20) | Canal d'envoi : `EMAIL`, `SMS`, `IN_APP` |
| `lu` | BOOLEAN | Indique si la notification a été lue |
| `date_envoi` | DATETIME | Date d'envoi |
| `date_lecture` | DATETIME | Date de lecture |
| `lien` | VARCHAR(500) | Lien vers la ressource concernée |
| `metadata` | TEXT | Métadonnées JSON supplémentaires |

### Relations

**Entrantes :**
- **N → 1** : `Utilisateur` (destinataire) - Une notification est envoyée à un utilisateur

---

## 🔗 DIAGRAMME DES RELATIONS

### Légende des cardinalités
- **1 → N** : Un à plusieurs
- **N → 1** : Plusieurs à un
- **1 → 1** : Un à un
- **N → N** : Plusieurs à plusieurs

### Relations principales

```
UTILISATEUR (1) ──creates──> (N) FAMILLE
UTILISATEUR (N) ──belongs_to──> (N) FAMILLE via MEMBREFAMILLE
UTILISATEUR (1) ──sends──> (N) INVITATION
UTILISATEUR (1) ──creates──> (N) CONTENU
UTILISATEUR (1) ──creates──> (N) QUIZ
UTILISATEUR (1) ──takes──> (N) RESULTATQUIZ
UTILISATEUR (1) ──creates──> (N) ARBREGENE ALOGIQUE
UTILISATEUR (1) ──receives──> (N) NOTIFICATION

FAMILLE (1) ──has──> (N) MEMBREFAMILLE
FAMILLE (1) ──has──> (N) INVITATION
FAMILLE (1) ──has──> (N) CONTENU
FAMILLE (1) ──has──> (N) QUIZ
FAMILLE (1) ──has──> (N) ARBREGENE ALOGIQUE

CATEGORIE (1) ──categorizes──> (N) CONTENU

CONTENU (1) ──has──> (N) TRADUCTIONCONTENU
CONTENU (1) ──has──> (N) DEMANDEPUBLICATION
CONTENU (1) ──has──> (N) QUIZ  ⚠️ UN CONTENU PEUT AVOIR PLUSIEURS QUIZ

QUIZ (1) ──has──> (N) QUESTIONQUIZ
QUIZ (1) ──has──> (N) RESULTATQUIZ

QUESTIONQUIZ (1) ──has──> (N) PROPOSITION

ARBREGENE ALOGIQUE (1) ──has──> (N) MEMBREARBRE

MEMBREARBRE (N) ──has_father──> (1) MEMBREARBRE (auto-référence)
MEMBREARBRE (N) ──has_mother──> (1) MEMBREARBRE (auto-référence)
MEMBREARBRE (N) ──linked_to──> (1) UTILISATEUR
```

---

## 📌 RELATIONS DÉTAILLÉES PAR TABLE

### UTILISATEUR
- **Crée** → Famille (1:N)
- **Appartient à** → Famille via MembreFamille (N:N)
- **Envoie** → Invitation (1:N)
- **Accepte** → Invitation (1:N)
- **Crée** → Contenu (1:N)
- **Demande publication** → DemandePublication (1:N)
- **Valide publication** → DemandePublication (1:N)
- **Crée** → Quiz (1:N)
- **Passe** → ResultatQuiz (1:N)
- **Crée** → ArbreGenealogique (1:N)
- **Lié à** → MembreArbre (1:N)
- **Reçoit** → Notification (1:N)

### FAMILLE
- **Créée par** → Utilisateur (N:1)
- **A** → MembreFamille (1:N)
- **A** → Invitation (1:N)
- **A** → Contenu (1:N)
- **A** → Quiz (1:N)
- **A** → ArbreGenealogique (1:N)

### CONTENU
- **Appartient à** → Famille (N:1)
- **Créé par** → Utilisateur (N:1)
- **Catégorisé par** → Categorie (N:1)
- **A** → TraductionContenu (1:N)
- **A** → DemandePublication (1:N)
- **A** → Quiz (1:N) ⚠️ **RELATION IMPORTANTE**

### QUIZ
- **Appartient à** → Famille (N:1)
- **Lié à** → Contenu (N:1) **OPTIONNEL**
- **Créé par** → Utilisateur (N:1)
- **A** → QuestionQuiz (1:N)
- **A** → ResultatQuiz (1:N)

---

## 🎯 CAS D'USAGE IMPORTANTS

### 1. Création d'une famille
1. Utilisateur crée une Famille → devient créateur
2. Système crée automatiquement un MembreFamille avec role `ADMIN`

### 2. Invitation de membres
1. ADMIN crée une Invitation avec `code_invitation` unique
2. Invité reçoit le code par email/SMS
3. Invité s'inscrit ou se connecte et utilise le code
4. Système crée un MembreFamille avec le role spécifié

### 3. Publication de contenu
1. ADMIN/EDITEUR crée un Contenu avec statut `BROUILLON`
2. ADMIN famille demande la publication → crée DemandePublication (`EN_ATTENTE`)
3. SUPERADMIN valide → DemandePublication passe à `APPROUVEE`
4. Contenu passe à statut `PUBLIE`

### 4. Création de quiz sur un contenu
1. ADMIN/EDITEUR crée un Contenu (ex: un conte)
2. ADMIN/EDITEUR crée un Quiz et le lie au Contenu via `id_contenu`
3. Quiz est créé avec des QuestionQuiz et leurs Proposition
4. **Un même contenu peut avoir plusieurs quiz différents**

### 5. Arbre généalogique
1. ADMIN crée un ArbreGenealogique pour la Famille
2. Ajoute des MembreArbre avec relations père/mère (auto-référence)
3. Peut lier un MembreArbre à un Utilisateur réel

---

## 📊 STATISTIQUES IMPORTANTES

### Nombre de tables : **15 tables**

### Types de relations :
- **1 → N** : 38 relations
- **N → N** : 1 relation (Utilisateur ↔ Famille via MembreFamille)
- **Auto-référence** : 2 relations (MembreArbre père/mère)

### Clés étrangères : **31 FK**

### Index : **47 index** (pour optimisation des requêtes)

---

## 🔐 RÈGLES DE SÉCURITÉ

### Suppressions en cascade (ON DELETE CASCADE)
- Suppression d'une Famille → supprime tous ses MembreFamille, Contenu, Quiz, Invitation
- Suppression d'un Contenu → supprime ses TraductionContenu, DemandePublication
- Suppression d'un Quiz → supprime ses QuestionQuiz, Proposition, ResultatQuiz
- Suppression d'un Utilisateur → supprime ses MembreFamille, Notification

### Suppressions restreintes (ON DELETE RESTRICT)
- Suppression d'un Utilisateur créateur → **empêchée** si il a créé des Famille, Contenu, Quiz, Arbre
- Permet de préserver l'intégrité des données historiques

---

## 📝 NOTES TECHNIQUES

### Types de données
- **BIGINT** : Identifiants (support jusqu'à 9 quintillions)
- **VARCHAR** : Chaînes de caractères de longueur variable
- **TEXT** : Texte long (descriptions, biographies)
- **ENUM** : Valeurs prédéfinies (statuts, rôles)
- **DATETIME** : Dates avec heure
- **DATE** : Dates seules
- **BOOLEAN** : Valeurs true/false

### Encodage
- **Charset** : utf8mb4 (support complet Unicode, émojis)
- **Collation** : utf8mb4_unicode_ci (insensible à la casse)
- **Engine** : InnoDB (support transactions, clés étrangères)

---

## 📅 GESTION DES DATES

Toutes les tables principales ont :
- `date_creation` : Date de création (auto-générée)
- `date_modification` : Date de modification (auto-mise à jour)

---

## 🎨 LÉGENDE DES ICÔNES

- 👤 Utilisateur
- 👨‍👩‍👧‍👦 Famille
- 🔗 Relation/Lien
- 📧 Invitation/Message
- 🏷️ Catégorie/Tag
- 📚 Contenu
- 🌍 Traduction
- 📝 Demande/Document
- 🎯 Quiz
- ❓ Question
- ✅ Réponse/Proposition
- 📊 Résultat/Statistique
- 🌳 Arbre généalogique
- 👥 Membre
- 🔔 Notification

---

**Document généré le** : 2025-11-10  
**Version de la base de données** : 1.0  
**Dernière mise à jour** : Ajout du champ `id_contenu` dans la table `quiz`

