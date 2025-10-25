# 🔗 **RELATIONS ENTRE CLASSES - HERITAGE NUMÉRIQUE**

## 📋 **Vue d'Ensemble des Relations**

Ce document décrit toutes les relations entre les entités de l'application Heritage Numérique, incluant les cardinalités et les explications des associations.

---

## 👤 **UTILISATEUR (Utilisateur)**

### **Relations Sortantes :**
- **1:N** → `MembreFamille` : Un utilisateur peut être membre de plusieurs familles
- **1:N** → `Contenu` : Un utilisateur peut créer plusieurs contenus
- **1:N** → `Invitation` (émetteur) : Un utilisateur peut envoyer plusieurs invitations
- **1:N** → `Invitation` (invité) : Un utilisateur peut recevoir plusieurs invitations
- **1:N** → `QuizContenu` : Un utilisateur peut créer plusieurs quiz
- **1:N** → `ReponseUtilisateurQuiz` : Un utilisateur peut répondre à plusieurs quiz
- **1:N** → `ScoreUtilisateur` : Un utilisateur a un score global
- **1:N** → `ArbreGenealogique` : Un utilisateur peut créer plusieurs arbres
- **1:N** → `MembreArbre` : Un utilisateur peut être dans plusieurs arbres

### **Relations Entrantes :**
- **N:1** ← `Famille` (créateur) : Une famille a un créateur
- **N:1** ← `MembreFamille` : Un membre de famille appartient à un utilisateur

---

## 👨‍👩‍👧‍👦 **FAMILLE (Famille)**

### **Relations Sortantes :**
- **1:N** → `MembreFamille` : Une famille a plusieurs membres
- **1:N** → `Contenu` : Une famille peut avoir plusieurs contenus
- **1:N** → `Invitation` : Une famille peut envoyer plusieurs invitations
- **1:N** → `ArbreGenealogique` : Une famille a un arbre généalogique
- **1:N** → `MembreArbre` : Une famille a plusieurs membres dans l'arbre

### **Relations Entrantes :**
- **N:1** ← `Utilisateur` (créateur) : Une famille a un créateur

---

## 👥 **MEMBRE_FAMILLE (MembreFamille)**

### **Relations Sortantes :**
- **1:N** → `Contenu` : Un membre peut créer plusieurs contenus

### **Relations Entrantes :**
- **N:1** ← `Utilisateur` : Un membre appartient à un utilisateur
- **N:1** ← `Famille` : Un membre appartient à une famille

---

## 📚 **CONTENU (Contenu)**

### **Relations Sortantes :**
- **1:N** → `TraductionContenu` : Un contenu peut avoir plusieurs traductions
- **1:N** → `QuizContenu` : Un contenu peut avoir plusieurs quiz
- **1:N** → `QuestionQuiz` : Un quiz a plusieurs questions

### **Relations Entrantes :**
- **N:1** ← `Utilisateur` (auteur) : Un contenu a un auteur
- **N:1** ← `Famille` : Un contenu appartient à une famille
- **N:1** ← `Categorie` : Un contenu appartient à une catégorie

---

## 📧 **INVITATION (Invitation)**

### **Relations Sortantes :**
- **1:1** → `Utilisateur` (invité) : Une invitation peut être acceptée par un utilisateur

### **Relations Entrantes :**
- **N:1** ← `Utilisateur` (émetteur) : Une invitation est envoyée par un utilisateur
- **N:1** ← `Famille` : Une invitation est pour une famille

---

## 🎯 **QUIZ_CONTENU (QuizContenu)**

### **Relations Sortantes :**
- **1:N** → `QuestionQuiz` : Un quiz a plusieurs questions
- **1:N** → `ReponseUtilisateurQuiz` : Un quiz peut avoir plusieurs réponses

### **Relations Entrantes :**
- **N:1** ← `Utilisateur` (créateur) : Un quiz a un créateur
- **N:1** ← `Contenu` : Un quiz est associé à un contenu

---

## ❓ **QUESTION_QUIZ (QuestionQuiz)**

### **Relations Sortantes :**
- **1:N** → `PropositionQuestion` : Une question a plusieurs propositions
- **1:N** → `ReponseUtilisateurQuiz` : Une question peut avoir plusieurs réponses

### **Relations Entrantes :**
- **N:1** ← `QuizContenu` : Une question appartient à un quiz

---

## 📝 **PROPOSITION_QUESTION (PropositionQuestion)**

### **Relations Sortantes :**
- **1:N** → `ReponseUtilisateurQuiz` : Une proposition peut être sélectionnée plusieurs fois

### **Relations Entrantes :**
- **N:1** ← `QuestionQuiz` : Une proposition appartient à une question

---

## ✅ **REPONSE_UTILISATEUR_QUIZ (ReponseUtilisateurQuiz)**

### **Relations Entrantes :**
- **N:1** ← `Utilisateur` : Une réponse appartient à un utilisateur
- **N:1** ← `QuizContenu` : Une réponse appartient à un quiz
- **N:1** ← `QuestionQuiz` : Une réponse est pour une question
- **N:1** ← `PropositionQuestion` : Une réponse peut sélectionner une proposition

---

## 🏆 **SCORE_UTILISATEUR (ScoreUtilisateur)**

### **Relations Entrantes :**
- **N:1** ← `Utilisateur` : Un score appartient à un utilisateur

---

## 🌳 **ARBRE_GENEALOGIQUE (ArbreGenealogique)**

### **Relations Sortantes :**
- **1:N** → `MembreArbre` : Un arbre a plusieurs membres

### **Relations Entrantes :**
- **N:1** ← `Famille` : Un arbre appartient à une famille
- **N:1** ← `Utilisateur` (créateur) : Un arbre a un créateur

---

## 👨‍👩‍👧‍👦 **MEMBRE_ARBRE (MembreArbre)**

### **Relations Sortantes :**
- **1:N** → `MembreArbre` (parent1) : Un membre peut être parent de plusieurs autres
- **1:N** → `MembreArbre` (parent2) : Un membre peut être parent de plusieurs autres
- **1:N** → `MembreArbre` (conjoint) : Un membre peut être conjoint de plusieurs autres

### **Relations Entrantes :**
- **N:1** ← `ArbreGenealogique` : Un membre appartient à un arbre
- **N:1** ← `Famille` : Un membre appartient à une famille
- **N:1** ← `MembreArbre` (parent1) : Un membre a un parent1
- **N:1** ← `MembreArbre` (parent2) : Un membre a un parent2
- **N:1** ← `MembreArbre` (conjoint) : Un membre a un conjoint

---

## 🌍 **TRADUCTION_CONTENU (TraductionContenu)**

### **Relations Entrantes :**
- **N:1** ← `Contenu` : Une traduction appartient à un contenu

---

## 📊 **DIAGRAMME DES RELATIONS**

```
┌─────────────────┐    1:N    ┌─────────────────┐
│   UTILISATEUR   │──────────→│ MEMBRE_FAMILLE  │
└─────────────────┘           └─────────────────┘
         │                              │
         │ 1:N                          │ N:1
         ↓                              ↓
┌─────────────────┐    1:N    ┌─────────────────┐
│    CONTENU      │←───────────│    FAMILLE       │
└─────────────────┘           └─────────────────┘
         │                              │
         │ 1:N                          │ 1:N
         ↓                              ↓
┌─────────────────┐           ┌─────────────────┐
│  QUIZ_CONTENU   │           │INVITATION       │
└─────────────────┘           └─────────────────┘
         │
         │ 1:N
         ↓
┌─────────────────┐
│ QUESTION_QUIZ    │
└─────────────────┘
         │
         │ 1:N
         ↓
┌─────────────────┐
│PROPOSITION_QUESTION│
└─────────────────┘
```

---

## 🔍 **DÉTAIL DES RELATIONS**

### **1. Utilisateur ↔ Famille (N:N via MembreFamille)**
- **Explication** : Un utilisateur peut appartenir à plusieurs familles
- **Cardinalité** : N:N
- **Table de liaison** : `MembreFamille`
- **Rôles** : ADMIN, EDITEUR, LECTEUR

### **2. Utilisateur ↔ Contenu (1:N)**
- **Explication** : Un utilisateur peut créer plusieurs contenus
- **Cardinalité** : 1:N
- **Relation directe** : `Contenu.auteur`

### **3. Famille ↔ Contenu (1:N)**
- **Explication** : Une famille peut avoir plusieurs contenus
- **Cardinalité** : 1:N
- **Relation directe** : `Contenu.famille`

### **4. Contenu ↔ Quiz (1:N)**
- **Explication** : Un contenu peut avoir plusieurs quiz
- **Cardinalité** : 1:N
- **Relation directe** : `QuizContenu.contenu`

### **5. Quiz ↔ Question (1:N)**
- **Explication** : Un quiz peut avoir plusieurs questions
- **Cardinalité** : 1:N
- **Relation directe** : `QuestionQuiz.quiz`

### **6. Question ↔ Proposition (1:N)**
- **Explication** : Une question peut avoir plusieurs propositions
- **Cardinalité** : 1:N
- **Relation directe** : `PropositionQuestion.question`

### **7. Utilisateur ↔ Quiz (N:N via ReponseUtilisateurQuiz)**
- **Explication** : Un utilisateur peut répondre à plusieurs quiz
- **Cardinalité** : N:N
- **Table de liaison** : `ReponseUtilisateurQuiz`

### **8. Famille ↔ Arbre (1:1)**
- **Explication** : Une famille a un seul arbre généalogique
- **Cardinalité** : 1:1
- **Relation directe** : `ArbreGenealogique.famille`

### **9. Arbre ↔ MembreArbre (1:N)**
- **Explication** : Un arbre peut contenir plusieurs membres
- **Cardinalité** : 1:N
- **Relation directe** : `MembreArbre.arbre`

### **10. MembreArbre ↔ MembreArbre (Auto-référence)**
- **Explication** : Un membre peut avoir des parents et un conjoint
- **Cardinalité** : 1:N (parent1, parent2, conjoint)
- **Relation directe** : `MembreArbre.parent1`, `MembreArbre.parent2`, `MembreArbre.conjoint`

---

## 🎯 **RÈGLES MÉTIER**

### **1. Gestion des Rôles**
- **ADMIN famille** : Peut tout faire dans sa famille
- **EDITEUR famille** : Peut créer du contenu
- **LECTEUR famille** : Peut seulement lire

### **2. Gestion des Invitations**
- **Statut** : EN_ATTENTE, ACCEPTEE, REFUSEE
- **Expiration** : 48 heures
- **Code unique** : Généré automatiquement

### **3. Gestion des Quiz**
- **Quiz familiaux** : Créés par ADMIN/EDITEUR pour contenus familiaux
- **Quiz publics** : Créés par SUPER-ADMIN pour contenus publics
- **Types de questions** : QCM, Vrai/Faux

### **4. Gestion des Arbres**
- **Un arbre par famille** : Chaque famille a un seul arbre
- **Relations familiales** : Parent1, Parent2, Conjoint
- **Photos** : Upload de photos pour chaque membre

### **5. Gestion des Traductions**
- **Langues supportées** : Français, Bambara, Anglais
- **Types de contenus** : Contes, Artisanats, Proverbes, Devinettes
- **Service** : HuggingFace NLLB-200

---

## 📈 **CROISSANCE ET ÉVOLUTION**

### **1. Scalabilité**
- **Utilisateurs** : Support de milliers d'utilisateurs
- **Familles** : Support de centaines de familles
- **Contenus** : Support de milliers de contenus
- **Quiz** : Support de centaines de quiz

### **2. Performance**
- **Indexation** : Index sur les champs fréquemment utilisés
- **Pagination** : Pagination des listes longues
- **Cache** : Mise en cache des données fréquentes

### **3. Sécurité**
- **Authentification** : JWT tokens
- **Autorisation** : Contrôle d'accès basé sur les rôles
- **Validation** : Validation des données d'entrée

---

## 🎉 **Conclusion**

L'architecture des relations entre classes permet :

- ✅ **Flexibilité** : Relations modulaires et extensibles
- ✅ **Intégrité** : Contraintes de clés étrangères
- ✅ **Performance** : Indexation et optimisation
- ✅ **Sécurité** : Contrôle d'accès granulaire
- ✅ **Évolutivité** : Support de la croissance

Cette structure garantit la cohérence des données et facilite l'évolution de l'application.
