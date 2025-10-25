# 📁 **DOCUMENTATION DES FICHIERS - HERITAGE NUMÉRIQUE**

## 🎯 **Vue d'Ensemble**

Ce document explique le rôle de chaque fichier dans l'application Heritage Numérique, leur structure et leur contribution à l'architecture globale.

---

## 🏗️ **ARCHITECTURE MVC**

L'application suit l'architecture **MVC (Model-View-Controller)** avec Spring Boot :

- **Model** : Entités JPA et Repositories
- **View** : Controllers REST et DTOs
- **Controller** : Services et logique métier

---

## 📂 **STRUCTURE DES PACKAGES**

```
src/main/java/com/heritage/
├── controller/          # Contrôleurs REST
├── service/            # Services métier
├── repository/         # Repositories JPA
├── entite/            # Entités JPA
├── dto/               # Data Transfer Objects
├── securite/          # Configuration sécurité
├── exception/         # Gestion des exceptions
└── util/              # Classes utilitaires
```

---

## 🎮 **CONTROLLERS (Couche Présentation)**

### **1. AuthController.java**
- **Rôle** : Gestion de l'authentification
- **Endpoints** : `/api/auth/*`
- **Fonctionnalités** :
  - Inscription utilisateur
  - Connexion utilisateur
  - Connexion avec code d'invitation
- **Sécurité** : Public (pas d'authentification requise)

### **2. FamilleController.java**
- **Rôle** : Gestion des familles
- **Endpoints** : `/api/familles/*`
- **Fonctionnalités** :
  - Créer une famille
  - Modifier une famille
  - Supprimer une famille
- **Sécurité** : Authentification requise

### **3. InvitationController.java**
- **Rôle** : Gestion des invitations
- **Endpoints** : `/api/invitations/*`
- **Fonctionnalités** :
  - Envoyer une invitation
  - Accepter une invitation
  - Refuser une invitation
- **Sécurité** : Authentification requise

### **4. ContenuController.java**
- **Rôle** : Gestion des contenus
- **Endpoints** : `/api/contenus/*`
- **Fonctionnalités** :
  - Créer du contenu
  - Modifier du contenu
  - Supprimer du contenu
- **Sécurité** : Authentification requise

### **5. QuizController.java**
- **Rôle** : Gestion des quiz
- **Endpoints** : `/api/quiz/*`
- **Fonctionnalités** :
  - Créer un quiz
  - Ajouter des questions
  - Ajouter des propositions
  - Répondre à un quiz
- **Sécurité** : Authentification requise

### **6. ArbreGenealogiqueController.java**
- **Rôle** : Gestion des arbres généalogiques
- **Endpoints** : `/api/arbre-genealogique/*`
- **Fonctionnalités** :
  - Créer un arbre
  - Ajouter des membres
  - Voir l'arbre
- **Sécurité** : Authentification requise

### **7. DashboardController.java**
- **Rôle** : Dashboard familial
- **Endpoints** : `/api/dashboard/*`
- **Fonctionnalités** :
  - Statistiques familiales
  - Contenus récents
  - Membres actifs
- **Sécurité** : Authentification requise

### **8. DashboardPersonnelController.java**
- **Rôle** : Dashboard personnel
- **Endpoints** : `/api/dashboard-personnel/*`
- **Fonctionnalités** :
  - Statistiques personnelles
  - Invitations en attente
  - Familles d'appartenance
- **Sécurité** : Authentification requise

### **9. SuperAdminController.java**
- **Rôle** : Gestion super-admin
- **Endpoints** : `/api/superadmin/*`
- **Fonctionnalités** :
  - Statistiques globales
  - Validation de publications
  - Gestion des catégories
- **Sécurité** : Rôle ADMIN requis

### **10. SuperAdminDashboardController.java**
- **Rôle** : Dashboard super-admin
- **Endpoints** : `/api/superadmin/dashboard/*`
- **Fonctionnalités** :
  - Vue d'ensemble complète
  - Toutes les familles
  - Tous les contenus
  - Quiz publics
- **Sécurité** : Rôle ADMIN requis

### **11. TraductionUnifieeController.java**
- **Rôle** : Traduction de contenus
- **Endpoints** : `/api/traduction/*`
- **Fonctionnalités** :
  - Traduire des contes
  - Traduire des artisanats
  - Traduire des proverbes
  - Traduire des devinettes
- **Sécurité** : Authentification requise

---

## 🔧 **SERVICES (Couche Métier)**

### **1. AuthService.java**
- **Rôle** : Logique d'authentification
- **Fonctionnalités** :
  - Inscription utilisateur
  - Connexion utilisateur
  - Génération de tokens JWT
  - Validation des invitations
- **Dépendances** : UtilisateurRepository, JwtService

### **2. FamilleService.java**
- **Rôle** : Logique des familles
- **Fonctionnalités** :
  - Création de familles
  - Gestion des membres
  - Changement de rôles
- **Dépendances** : FamilleRepository, MembreFamilleRepository

### **3. InvitationService.java**
- **Rôle** : Logique des invitations
- **Fonctionnalités** :
  - Envoi d'invitations
  - Gestion des codes
  - Expiration automatique
- **Dépendances** : InvitationRepository, EmailService

### **4. ContenuService.java**
- **Rôle** : Logique des contenus
- **Fonctionnalités** :
  - Création de contenus
  - Gestion des publications
  - Validation des permissions
- **Dépendances** : ContenuRepository, MembreFamilleRepository

### **5. QuizService.java**
- **Rôle** : Logique des quiz
- **Fonctionnalités** :
  - Création de quiz
  - Gestion des questions
  - Calcul des scores
- **Dépendances** : QuizRepository, QuestionRepository

### **6. ArbreGenealogiqueService.java**
- **Rôle** : Logique des arbres
- **Fonctionnalités** :
  - Création d'arbres
  - Gestion des membres
  - Relations familiales
- **Dépendances** : ArbreRepository, MembreArbreRepository

### **7. DashboardService.java**
- **Rôle** : Logique du dashboard familial
- **Fonctionnalités** :
  - Calcul des statistiques
  - Agrégation des données
- **Dépendances** : Multiple repositories

### **8. DashboardPersonnelService.java**
- **Rôle** : Logique du dashboard personnel
- **Fonctionnalités** :
  - Statistiques personnelles
  - Invitations en attente
- **Dépendances** : Multiple repositories

### **9. SuperAdminDashboardService.java**
- **Rôle** : Logique du dashboard super-admin
- **Fonctionnalités** :
  - Statistiques globales
  - Vue d'ensemble complète
- **Dépendances** : Multiple repositories

### **10. ServiceTraductionHuggingFace.java**
- **Rôle** : Service de traduction principal
- **Fonctionnalités** :
  - Traduction avec HuggingFace NLLB-200
  - Support multilingue
  - Gestion des erreurs
- **Dépendances** : HuggingFace API

### **11. EmailService.java**
- **Rôle** : Service d'envoi d'emails
- **Fonctionnalités** :
  - Envoi d'invitations
  - Envoi de codes de connexion
  - Templates d'emails
- **Dépendances** : JavaMailSender

---

## 🗄️ **REPOSITORIES (Couche Données)**

### **1. UtilisateurRepository.java**
- **Rôle** : Accès aux données utilisateurs
- **Méthodes** : findByEmail, findByRole, count
- **Entité** : Utilisateur

### **2. FamilleRepository.java**
- **Rôle** : Accès aux données familles
- **Méthodes** : findByCreateur, count
- **Entité** : Famille

### **3. MembreFamilleRepository.java**
- **Rôle** : Accès aux données membres
- **Méthodes** : findByFamille, findByUtilisateur
- **Entité** : MembreFamille

### **4. ContenuRepository.java**
- **Rôle** : Accès aux données contenus
- **Méthodes** : findByFamille, findByType, countByType
- **Entité** : Contenu

### **5. InvitationRepository.java**
- **Rôle** : Accès aux données invitations
- **Méthodes** : findByCode, findByStatut
- **Entité** : Invitation

### **6. QuizRepository.java**
- **Rôle** : Accès aux données quiz
- **Méthodes** : findByFamille, findByType
- **Entité** : Quiz

### **7. QuestionRepository.java**
- **Rôle** : Accès aux données questions
- **Méthodes** : findByQuiz, countByQuiz
- **Entité** : QuestionQuiz

### **8. PropositionRepository.java**
- **Rôle** : Accès aux données propositions
- **Méthodes** : findByQuestion, findByCorrecte
- **Entité** : PropositionQuestion

### **9. ArbreGenealogiqueRepository.java**
- **Rôle** : Accès aux données arbres
- **Méthodes** : findByFamille, countByFamille
- **Entité** : ArbreGenealogique

### **10. MembreArbreRepository.java**
- **Rôle** : Accès aux données membres d'arbre
- **Méthodes** : findByArbre, findByFamille
- **Entité** : MembreArbre

---

## 🏛️ **ENTITÉS (Modèle de Données)**

### **1. Utilisateur.java**
- **Rôle** : Représente un utilisateur
- **Champs** : id, nom, prenom, email, motDePasse, role, numeroTelephone, ethnie
- **Relations** : 1:N avec MembreFamille, Contenu, Invitation

### **2. Famille.java**
- **Rôle** : Représente une famille
- **Champs** : id, nom, description, ethnie, region, dateCreation, createur
- **Relations** : 1:N avec MembreFamille, Contenu, Invitation

### **3. MembreFamille.java**
- **Rôle** : Représente l'appartenance à une famille
- **Champs** : id, utilisateur, famille, roleFamille, lienParente, dateAjout
- **Relations** : N:1 avec Utilisateur, Famille

### **4. Contenu.java**
- **Rôle** : Représente un contenu
- **Champs** : id, titre, description, typeContenu, statut, auteur, famille
- **Relations** : N:1 avec Utilisateur, Famille

### **5. Invitation.java**
- **Rôle** : Représente une invitation
- **Champs** : id, code, statut, emetteur, famille, invite, dateExpiration
- **Relations** : N:1 avec Utilisateur, Famille

### **6. Quiz.java**
- **Rôle** : Représente un quiz
- **Champs** : id, titre, description, typeQuiz, statut, createur, contenu
- **Relations** : N:1 avec Utilisateur, Contenu

### **7. QuestionQuiz.java**
- **Rôle** : Représente une question
- **Champs** : id, question, typeReponse, quiz
- **Relations** : N:1 avec Quiz

### **8. PropositionQuestion.java**
- **Rôle** : Représente une proposition
- **Champs** : id, texte, estCorrecte, question
- **Relations** : N:1 avec QuestionQuiz

### **9. ArbreGenealogique.java**
- **Rôle** : Représente un arbre généalogique
- **Champs** : id, nom, description, famille, createur
- **Relations** : N:1 avec Famille, Utilisateur

### **10. MembreArbre.java**
- **Rôle** : Représente un membre d'arbre
- **Champs** : id, nomComplet, dateNaissance, lieuNaissance, relationFamille
- **Relations** : N:1 avec ArbreGenealogique, Famille

---

## 📦 **DTOs (Data Transfer Objects)**

### **1. AuthResponse.java**
- **Rôle** : Réponse d'authentification
- **Champs** : accessToken, tokenType, userId, email, nom, prenom, role

### **2. FamilleDTO.java**
- **Rôle** : Données de famille
- **Champs** : id, nom, description, ethnie, region, createur, dateCreation

### **3. ContenuDTO.java**
- **Rôle** : Données de contenu
- **Champs** : id, titre, description, typeContenu, statut, auteur, famille

### **4. QuizDTO.java**
- **Rôle** : Données de quiz
- **Champs** : id, titre, description, typeQuiz, statut, createur, contenu

### **5. DashboardDTO.java**
- **Rôle** : Données du dashboard
- **Champs** : idFamille, nomFamille, nombreMembres, nombreContenus, nombreQuiz

### **6. SuperAdminDashboardDTO.java**
- **Rôle** : Données du dashboard super-admin
- **Champs** : nombreUtilisateurs, nombreFamilles, nombreContenus, contenusRecents

### **7. TraductionConteDTO.java**
- **Rôle** : Données de traduction
- **Champs** : idConte, titreOriginal, contenuOriginal, traductionsTitre, traductionsContenu

---

## 🔐 **SÉCURITÉ**

### **1. JwtService.java**
- **Rôle** : Gestion des tokens JWT
- **Fonctionnalités** :
  - Génération de tokens
  - Validation de tokens
  - Extraction d'informations
- **Configuration** : JwtProperties

### **2. JwtAuthenticationFilter.java**
- **Rôle** : Filtre d'authentification JWT
- **Fonctionnalités** :
  - Interception des requêtes
  - Validation des tokens
  - Configuration du contexte de sécurité
- **Dépendances** : JwtService

### **3. SecurityConfig.java**
- **Rôle** : Configuration de la sécurité
- **Fonctionnalités** :
  - Configuration des filtres
  - Règles d'autorisation
  - Gestion des CORS
- **Dépendances** : JwtAuthenticationFilter

### **4. CustomUserDetailsService.java**
- **Rôle** : Service de détails utilisateur
- **Fonctionnalités** :
  - Chargement des utilisateurs
  - Configuration des autorités
  - Gestion des rôles
- **Dépendances** : UtilisateurRepository

### **5. UserPrincipal.java**
- **Rôle** : Principal utilisateur
- **Fonctionnalités** :
  - Stockage des informations utilisateur
  - Gestion des autorités
  - Support JWT
- **Dépendances** : UserDetails

---

## 🛠️ **UTILITAIRES**

### **1. AuthenticationHelper.java**
- **Rôle** : Aide à l'authentification
- **Fonctionnalités** :
  - Extraction de l'ID utilisateur
  - Validation du contexte
  - Gestion des erreurs
- **Usage** : Dans les contrôleurs

### **2. SecurityUtils.java**
- **Rôle** : Utilitaires de sécurité
- **Fonctionnalités** :
  - Validation des permissions
  - Gestion des rôles
  - Vérification d'accès
- **Usage** : Dans les services

---

## 📋 **CONFIGURATION**

### **1. application.properties**
- **Rôle** : Configuration de l'application
- **Sections** :
  - Base de données MySQL
  - JWT configuration
  - Email configuration
  - HuggingFace configuration
  - Logging configuration

### **2. schema.sql**
- **Rôle** : Schéma de base de données
- **Contenu** :
  - Création des tables
  - Contraintes de clés étrangères
  - Index pour la performance
  - Données initiales

---

## 🎯 **PRINCIPES ARCHITECTURAUX**

### **1. Séparation des Responsabilités**
- ✅ **Controllers** : Gestion des requêtes HTTP
- ✅ **Services** : Logique métier
- ✅ **Repositories** : Accès aux données
- ✅ **DTOs** : Transfert de données

### **2. Inversion de Dépendance**
- ✅ **@Autowired** : Injection de dépendances
- ✅ **Interfaces** : Abstraction des repositories
- ✅ **Configuration** : Externalisée dans properties

### **3. Principe DRY (Don't Repeat Yourself)**
- ✅ **Services communs** : Réutilisation de la logique
- ✅ **DTOs génériques** : Structures communes
- ✅ **Utilitaires** : Fonctions partagées

### **4. Principe SOLID**
- ✅ **Single Responsibility** : Une classe, une responsabilité
- ✅ **Open/Closed** : Ouvert à l'extension, fermé à la modification
- ✅ **Liskov Substitution** : Substitution des interfaces
- ✅ **Interface Segregation** : Interfaces spécifiques
- ✅ **Dependency Inversion** : Dépendance des abstractions

---

## 🚀 **AVANTAGES DE L'ARCHITECTURE**

### **1. Maintenabilité**
- ✅ **Code modulaire** : Chaque classe a un rôle précis
- ✅ **Documentation** : Javadoc et annotations
- ✅ **Tests** : Structure testable

### **2. Extensibilité**
- ✅ **Nouveaux endpoints** : Ajout facile
- ✅ **Nouvelles fonctionnalités** : Services modulaires
- ✅ **Nouveaux types** : DTOs extensibles

### **3. Performance**
- ✅ **Lazy loading** : Chargement à la demande
- ✅ **Indexation** : Optimisation des requêtes
- ✅ **Cache** : Mise en cache des données

### **4. Sécurité**
- ✅ **JWT** : Authentification sécurisée
- ✅ **Autorisation** : Contrôle d'accès granulaire
- ✅ **Validation** : Validation des données

---

## 🎉 **Conclusion**

L'architecture Heritage Numérique est :

- ✅ **Modulaire** : Chaque composant a un rôle précis
- ✅ **Extensible** : Facile d'ajouter de nouvelles fonctionnalités
- ✅ **Maintenable** : Code organisé et documenté
- ✅ **Sécurisée** : Authentification et autorisation
- ✅ **Performante** : Optimisée pour la production

Cette structure garantit la qualité du code et facilite l'évolution de l'application.
