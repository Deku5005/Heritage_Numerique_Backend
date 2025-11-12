# 📡 API Endpoints - Heritage Numérique

## 🔄 **WORKFLOW COMPLET**

### **Étape 1 : Authentification**
- **1.1** Inscription simple (sans invitation)
- **1.2** Inscription avec code d'invitation
- **1.3** Connexion

### **Étape 2 : Gestion des Familles**
- **2.1** Créer une famille (devient ADMIN)
- **2.2** Inviter des membres
- **2.3** Gérer les rôles des membres

### **Étape 3 : Gestion des Invitations**
- **3.1** Voir ses invitations en attente
- **3.2** Accepter une invitation
- **3.3** Refuser une invitation

### **Étape 4 : Gestion des Contenus**
- **4.1** Créer du contenu (EDITEUR/ADMIN)
- **4.2** Demander publication publique (ADMIN uniquement)
- **4.3** Valider publication (SUPER-ADMIN)

### **Étape 5 : Traduction Automatique**
- **5.1** Traduire du contenu
- **5.2** Tester les services de traduction

---

## 🔐 **1. AUTHENTIFICATION**

### **1.1 Inscription Simple (Sans Invitation)**
```http
POST /api/auth/register
Content-Type: application/json

{
  "nom": "Traoré",
  "prenom": "Fatoumata",
  "email": "fatoumata@example.com",
  "numeroTelephone": "+223 70 12 34 56",
  "ethnie": "Bambara",
  "motDePasse": "password123"
}
```

**Réponse :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "fatoumata@example.com",
  "nom": "Traoré",
  "prenom": "Fatoumata",
  "role": "ROLE_MEMBRE"
}
```

### **1.2 Inscription avec Code d'Invitation**
```http
POST /api/auth/register
Content-Type: application/json

{
  "nom": "Traoré",
  "prenom": "Fatoumata",
  "email": "fatoumata@example.com",
  "numeroTelephone": "+223 70 12 34 56",
  "ethnie": "Bambara",
  "motDePasse": "password123",
  "codeInvitation": "ABC12345"
}
```

**Réponse :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "fatoumata@example.com",
  "nom": "Traoré",
  "prenom": "Fatoumata",
  "role": "ROLE_MEMBRE",
  "message": "Compte créé avec succès. Vérifiez vos invitations en attente dans votre dashboard."
}
```

**⚠️ Important :** Avec un code d'invitation, l'utilisateur :
1. ✅ Crée son compte
2. ✅ Devient membre de la famille (rôle LECTEUR)
3. ⚠️ L'invitation reste **EN_ATTENTE**
4. 📋 Doit accepter/refuser l'invitation depuis son dashboard personnel

### **1.3 Connexion**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "fatoumata@example.com",
  "motDePasse": "password123"
}
```

### **1.4 Connexion avec Code d'Invitation (Utilisateur Existant)**
```http
POST /api/auth/login-with-code
Content-Type: application/json

{
  "email": "utilisateur.existant@example.com",
  "motDePasse": "password123",
  "codeInvitation": "ABC12345"
}
```

**Réponse :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "fatoumata@example.com",
  "nom": "Traoré",
  "prenom": "Fatoumata",
  "role": "ROLE_MEMBRE"
}
```

---

## 👨‍👩‍👧‍👦 **2. GESTION DES FAMILLES**

### **2.1 Créer une Famille (Devient ADMIN)**
```http
POST /api/familles
Authorization: Bearer {token}
Content-Type: application/json

{
  "nom": "Famille Traoré",
  "description": "Famille élargie des Traoré de Bamako",
  "ethnie": "Bambara",
  "region": "District de Bamako"
}
```

**Réponse :**
```json
{
  "id": 1,
  "nom": "Famille Traoré",
  "description": "Famille élargie des Traoré de Bamako",
  "ethnie": "Bambara",
  "region": "District de Bamako",
  "idCreateur": 1,
  "nomCreateur": "Traoré Fatoumata",
  "dateCreation": "2024-01-15T10:30:00",
  "nombreMembres": 1
}
```

### **2.2 Inviter un Membre**
```http
POST /api/invitations
Authorization: Bearer {token}
Content-Type: application/json

{
  "nomInvite": "Fatoumata Traoré",
  "emailInvite": "fatoumata@example.com",
  "telephoneInvite": "+223 70 12 34 56",
  "lienParente": "Cousine",
  "idFamille": 1
}
```

**Réponse :**
```json
{
  "id": 1,
  "nomInvite": "Fatoumata Traoré",
  "emailInvite": "fatoumata@example.com",
  "telephoneInvite": "+223 70 12 34 56",
  "lienParente": "Cousine",
  "codeInvitation": "ABC12345",
  "statut": "EN_ATTENTE",
  "dateExpiration": "2024-01-17T10:30:00",
  "message": "Invitation envoyée avec succès"
}
```

### **2.3 Changer le Rôle d'un Membre**
```http
PUT /api/familles/{familleId}/changer-role
Authorization: Bearer {token}
Content-Type: application/json

{
  "utilisateurId": 2,
  "nouveauRole": "EDITEUR"
}
```

**Réponse :**
```json
{
  "message": "Rôle mis à jour avec succès",
  "nouveauRole": "EDITEUR"
}
```

### **2.4 Voir les Membres d'une Famille**
```http
GET /api/membres/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "idUtilisateur": 1,
    "nom": "Traoré",
    "prenom": "Amadou",
    "email": "amadou@example.com",
    "telephone": "+223 70 12 34 56",
    "ethnie": "Bambara",
    "roleFamille": "ADMIN",
    "lienParente": "Chef de famille",
    "dateAjout": "2024-01-10T09:00:00",
    "statut": "ACCEPTE",
    "idFamille": 1,
    "nomFamille": "Famille Traoré"
  },
  {
    "id": 2,
    "idUtilisateur": 2,
    "nom": "Traoré",
    "prenom": "Fatoumata",
    "email": "fatoumata@example.com",
    "telephone": "+223 70 12 34 57",
    "ethnie": "Bambara",
    "roleFamille": "EDITEUR",
    "lienParente": "Fille",
    "dateAjout": "2024-01-12T14:20:00",
    "statut": "ACCEPTE",
    "idFamille": 1,
    "nomFamille": "Famille Traoré"
  }
]
```

### **2.5 Ajouter un Membre Manuellement (ADMIN uniquement)**
```http
POST /api/membres/ajouter
Authorization: Bearer {token}
Content-Type: application/json

{
  "idFamille": 1,
  "nom": "Keita",
  "prenom": "Moussa",
  "email": "moussa@example.com",
  "telephone": "+223 70 12 34 58",
  "ethnie": "Bambara",
  "lienParente": "Cousin",
  "roleFamille": "LECTEUR"
}
```

**Réponse :**
```json
{
  "id": 3,
  "idUtilisateur": 3,
  "nom": "Keita",
  "prenom": "Moussa",
  "email": "moussa@example.com",
  "telephone": "+223 70 12 34 58",
  "ethnie": "Bambara",
  "roleFamille": "LECTEUR",
  "lienParente": "Cousin",
  "dateAjout": "2024-01-15T10:30:00",
  "statut": "ACCEPTE",
  "idFamille": 1,
  "nomFamille": "Famille Traoré"
}
```

### **2.6 Voir les Contributions d'une Famille**
```http
GET /api/contributions/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idFamille": 1,
  "nomFamille": "Famille Traoré",
  "descriptionFamille": "Famille traditionnelle du Mali",
  "ethnieFamille": "Bambara",
  "regionFamille": "District de Bamako",
  "totalMembres": 5,
  "totalContributions": 12,
  "totalContes": 5,
  "totalProverbes": 3,
  "totalArtisanats": 2,
  "totalDevinettes": 2,
  "contributionsMembres": [
    {
      "idMembre": 1,
      "idUtilisateur": 1,
      "nom": "Traoré",
      "prenom": "Amadou",
      "email": "amadou@example.com",
      "roleFamille": "ADMIN",
      "lienParente": "Chef de famille",
      "totalContributions": 8,
      "nombreContes": 3,
      "nombreProverbes": 2,
      "nombreArtisanats": 2,
      "nombreDevinettes": 1,
      "dateAjout": "10/01/2024"
    },
    {
      "idMembre": 2,
      "idUtilisateur": 2,
      "nom": "Traoré",
      "prenom": "Fatoumata",
      "email": "fatoumata@example.com",
      "roleFamille": "EDITEUR",
      "lienParente": "Fille",
      "totalContributions": 4,
      "nombreContes": 2,
      "nombreProverbes": 1,
      "nombreArtisanats": 0,
      "nombreDevinettes": 1,
      "dateAjout": "12/01/2024"
    }
  ]
}
```

### **2.7 Voir les Contributions d'un Membre**
```http
GET /api/contributions/famille/{familleId}/membre/{membreId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idMembre": 1,
  "idUtilisateur": 1,
  "nom": "Traoré",
  "prenom": "Amadou",
  "email": "amadou@example.com",
  "roleFamille": "ADMIN",
  "lienParente": "Chef de famille",
  "totalContributions": 8,
  "nombreContes": 3,
  "nombreProverbes": 2,
  "nombreArtisanats": 2,
  "nombreDevinettes": 1,
  "dateAjout": "10/01/2024"
}
```

---

## 📧 **3. GESTION DES INVITATIONS**

### **3.1 Voir ses Invitations en Attente**
```http
GET /api/dashboard-personnel/invitations
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "invitationsEnAttente": [
    {
      "id": 1,
      "nomInvite": "Fatoumata Traoré",
      "emailInvite": "fatoumata@example.com",
      "telephoneInvite": "+223 70 12 34 56",
      "lienParente": "Cousine",
      "codeInvitation": "ABC12345",
      "nomFamille": "Famille Traoré",
      "statut": "EN_ATTENTE",
      "dateCreation": "2024-01-15T10:30:00",
      "dateExpiration": "2024-01-17T10:30:00"
    }
  ]
}
```

### **3.2 Accepter une Invitation**
```http
POST /api/invitations/1/accepter
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "message": "Invitation acceptée avec succès",
  "statut": "ACCEPTEE"
}
```

### **3.3 Refuser une Invitation**
```http
POST /api/invitations/1/refuser
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "message": "Invitation refusée",
  "statut": "REFUSEE"
}
```

---

## 📚 **4. GESTION DES CONTENUS**

### **4.1 Créer un Conte (EDITEUR/ADMIN)**
```http
POST /api/contenus/conte
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "idFamille": 1,
  "idCategorie": 1,
  "titre": "Conte de la tortue et du lièvre",
  "description": "Conte traditionnel bambara",
  "texteConte": "Il était une fois une tortue et un lièvre...",
  "photoConte": [fichier_image],
  "fichierConte": [fichier_pdf_ou_txt],
  "lieu": "Bamako",
  "region": "District de Bamako"
}
```

**Réponse :**
```json
{
  "id": 1,
  "titre": "Conte de la tortue et du lièvre",
  "description": "Il était une fois une tortue et un lièvre...",
  "typeContenu": "CONTE",
  "statut": "BROUILLON",
  "dateCreation": "2024-01-15T10:30:00"
}
```

### **4.2 Créer un Artisanat (EDITEUR/ADMIN)**
```http
POST /api/contenus/artisanat
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "idFamille": 1,
  "idCategorie": 2,
  "titre": "Poterie traditionnelle",
  "description": "Techniques de poterie ancestrale",
  "photosArtisanat": [fichier_image1, fichier_image2],
  "videoArtisanat": [fichier_video],
  "lieu": "Ségou",
  "region": "Région de Ségou"
}
```

**Réponse :**
```json
{
  "id": 2,
  "titre": "Poterie traditionnelle",
  "description": "Techniques de poterie ancestrale\n\nPhotos: photos/pot1.jpg, photos/pot2.jpg",
  "typeContenu": "ARTISANAT",
  "statut": "BROUILLON",
  "dateCreation": "2024-01-15T10:30:00"
}
```

### **4.3 Créer un Proverbe (EDITEUR/ADMIN)**
```http
POST /api/contenus/proverbe
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "idFamille": 1,
  "idCategorie": 3,
  "titre": "Proverbe de la sagesse",
  "origineProverbe": "Bambara",
  "significationProverbe": "La patience est une vertu",
  "texteProverbe": "Kunun kan ka kɛrɛ",
  "photoProverbe": [fichier_image],
  "lieu": "Bamako",
  "region": "District de Bamako"
}
```

**Réponse :**
```json
{
  "id": 3,
  "titre": "Proverbe de la sagesse",
  "description": "Origine: Bambara\nSignification: La patience est une vertu\nProverbe: Kunun kan ka kɛrɛ",
  "typeContenu": "PROVERBE",
  "statut": "BROUILLON",
  "dateCreation": "2024-01-15T10:30:00"
}
```

### **4.4 Créer une Devinette (EDITEUR/ADMIN)**
```http
POST /api/contenus/devinette
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "idFamille": 1,
  "idCategorie": 4,
  "titre": "Devinette du baobab",
  "texteDevinette": "Qu'est-ce qui est grand et fort mais ne peut pas bouger ?",
  "reponseDevinette": "Le baobab",
  "photoDevinette": [fichier_image],
  "lieu": "Bamako",
  "region": "District de Bamako"
}
```

**Réponse :**
```json
{
  "id": 4,
  "titre": "Devinette du baobab",
  "description": "Devinette: Qu'est-ce qui est grand et fort mais ne peut pas bouger ?\nRéponse: Le baobab",
  "typeContenu": "DEVINETTE",
  "statut": "BROUILLON",
  "dateCreation": "2024-01-15T10:30:00"
}
```

### **4.5 Voir tous les Contes d'une Famille**
```http
GET /api/contes/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Conte de la tortue et du lièvre",
    "description": "Il était une fois une tortue et un lièvre...",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "roleAuteur": "ADMIN",
    "lienParenteAuteur": "Chef de famille",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "BROUILLON",
    "urlFichier": "fichiers/conte_tortue_lièvre.pdf",
    "urlPhoto": "photos/tortue_lièvre.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré"
  },
  {
    "id": 2,
    "titre": "Conte de l'araignée et l'éléphant",
    "description": "Histoire traditionnelle bambara",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Fatoumata",
    "emailAuteur": "fatoumata@example.com",
    "roleAuteur": "EDITEUR",
    "lienParenteAuteur": "Fille",
    "dateCreation": "2024-01-12T14:20:00",
    "statut": "PUBLIE",
    "urlFichier": null,
    "urlPhoto": "photos/araignee_elephant.jpg",
    "lieu": "Ségou",
    "region": "Région de Ségou",
    "idFamille": 1,
    "nomFamille": "Famille Traoré"
  }
]
```

### **4.6 Voir un Conte Spécifique**
```http
GET /api/contes/{conteId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "id": 1,
  "titre": "Conte de la tortue et du lièvre",
  "description": "Il était une fois une tortue et un lièvre...",
  "nomAuteur": "Traoré",
  "prenomAuteur": "Amadou",
  "emailAuteur": "amadou@example.com",
  "roleAuteur": "ADMIN",
  "lienParenteAuteur": "Chef de famille",
  "dateCreation": "2024-01-10T09:00:00",
  "statut": "BROUILLON",
  "urlFichier": "fichiers/conte_tortue_lièvre.pdf",
  "urlPhoto": "photos/tortue_lièvre.jpg",
  "lieu": "Bamako",
  "region": "District de Bamako",
  "idFamille": 1,
  "nomFamille": "Famille Traoré"
}
```

### **4.7 Voir les Contes d'un Membre Spécifique**
```http
GET /api/contes/famille/{familleId}/membre/{membreId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Conte de la tortue et du lièvre",
    "description": "Il était une fois une tortue et un lièvre...",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "roleAuteur": "ADMIN",
    "lienParenteAuteur": "Chef de famille",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "BROUILLON",
    "urlFichier": "fichiers/conte_tortue_lièvre.pdf",
    "urlPhoto": "photos/tortue_lièvre.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré"
  }
]
```

### **4.8 Voir toutes les Devinettes d'une Famille**
```http
GET /api/devinettes/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Devinette du baobab",
    "devinette": "Qu'est-ce qui est grand et fort mais ne peut pas bouger ?",
    "reponse": "Le baobab",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "roleAuteur": "ADMIN",
    "lienParenteAuteur": "Chef de famille",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "BROUILLON",
    "urlPhoto": "photos/baobab.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré"
  }
]
```

### **4.9 Voir toutes les Artisanats d'une Famille**
```http
GET /api/artisanats/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Poterie traditionnelle",
    "description": "Techniques de poterie ancestrale",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Fatoumata",
    "emailAuteur": "fatoumata@example.com",
    "roleAuteur": "EDITEUR",
    "lienParenteAuteur": "Fille",
    "dateCreation": "2024-01-12T14:20:00",
    "statut": "PUBLIE",
    "urlPhotos": ["photos/pot1.jpg", "photos/pot2.jpg"],
    "urlVideo": "videos/poterie_demo.mp4",
    "lieu": "Ségou",
    "region": "Région de Ségou",
    "idFamille": 1,
    "nomFamille": "Famille Traoré"
  }
]
```

### **4.10 Voir tous les Proverbes d'une Famille**
```http
GET /api/proverbes/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Proverbe de la sagesse",
    "proverbe": "Kunun kan ka kɛrɛ",
    "signification": "La patience est une vertu",
    "origine": "Bambara",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "roleAuteur": "ADMIN",
    "lienParenteAuteur": "Chef de famille",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "BROUILLON",
    "urlPhoto": "photos/sagesse.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré"
  }
]
```

### **4.2 Demander Publication Publique (ADMIN famille uniquement)**
```http
POST /api/contenus/1/demander-publication
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "message": "Demande de publication envoyée",
  "statut": "EN_ATTENTE_VALIDATION"
}
```

### **4.3 Valider Publication (SUPER-ADMIN)**
```http
POST /api/superadmin/demandes-publication/{demandeId}/valider
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "message": "Contenu publié avec succès",
  "statut": "PUBLIE"
}
```

---

## 🌐 **5. TRADUCTION AUTOMATIQUE**

### **5.1 Traduire du Contenu**
```http
POST /api/traductions/contenus/{contenuId}/automatique
Authorization: Bearer {token}
Content-Type: application/json

{
  "langueCible": "EN",
  "langueSource": "FR",
  "sauvegarder": true
}
```

**Réponse :**
```json
{
  "traductionId": 1,
  "contenuId": 1,
  "langueSource": "FR",
  "langueCible": "EN",
  "titreOriginal": "Conte de la tortue et du lièvre",
  "titreTraduit": "Tale of the tortoise and the hare",
  "descriptionOriginale": "Conte traditionnel bambara",
  "descriptionTraduite": "Traditional Bambara tale",
  "sauvegardee": true,
  "dateCreation": "2024-01-15T10:30:00",
  "tempsTraduction": 1250,
  "message": "Traduction effectuée avec succès",
  "succes": true,
  "erreur": null,
  "texteFichierOriginal": null,
  "texteFichierTraduit": null,
  "fichierTraduit": false
}
```

### **5.2 Tester les Services de Traduction**
```http
GET /api/traductions/services
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "services": [
    {
      "nom": "MyMemory API",
      "disponible": true,
      "languesSupportees": ["FR", "EN", "ES", "DE", "IT", "PT"]
    },
    {
      "nom": "LibreTranslate",
      "disponible": false,
      "erreur": "Service non accessible"
    },
    {
      "nom": "Service Bambara",
      "disponible": true,
      "languesSupportees": ["FR-BM", "EN-BM"]
    }
  ]
}
```

### **5.3 Tester la Traduction Bambara**
```http
GET /api/traductions/test-bambara
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "texteOriginal": "Bonjour, comment allez-vous ?",
  "traductions": [
    {
      "langue": "BM",
      "texteTraduit": "A ni ce, i ka kɛnɛya ?",
      "methode": "Service Bambara intelligent"
    }
  ]
}
```

---

## 📊 **6. DASHBOARD ET STATISTIQUES**

### **6.1 Dashboard Familial**
```http
GET /api/dashboard/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idFamille": 1,
  "nomFamille": "Famille Traoré",
  "nombreMembres": 5,
  "nombreInvitationsEnAttente": 2,
  "nombreContenusPrives": 12,
  "nombreContenusPublics": 3,
  "nombreQuizActifs": 4,
  "nombreNotificationsNonLues": 1,
  "nombreArbreGenealogiques": 2
}
```

### **6.2 Dashboard Personnel**
```http
GET /api/dashboard-personnel
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "userId": 1,
  "nom": "Traoré",
  "prenom": "Amadou",
  "email": "amadou.traore@example.com",
  "role": "ROLE_MEMBRE",
  "nombreFamillesAppartenance": 2,
  "nombreInvitationsEnAttente": 1,
  "nombreInvitationsRecues": 3,
  "nombreContenusCreés": 8,
  "nombreQuizCreés": 5,
  "nombreNotificationsNonLues": 2,
  "invitationsEnAttente": [
    {
      "id": 1,
      "nomFamille": "Famille Keita",
      "nomAdmin": "Moussa Keita",
      "codeInvitation": "ABC12345",
      "statut": "EN_ATTENTE",
      "dateCreation": "2024-01-15T10:30:00"
    }
  ],
  "familles": [
    {
      "id": 1,
      "nom": "Famille Traoré",
      "description": "Famille traditionnelle du Mali",
      "ethnie": "Bambara",
      "region": "District de Bamako",
      "nomAdmin": "Amadou Traoré",
      "dateCreation": "2024-01-10T09:00:00",
      "nombreMembres": 5
    },
    {
      "id": 2,
      "nom": "Famille Diallo",
      "description": "Famille peule du Sénégal",
      "ethnie": "Peul",
      "region": "Région de Dakar",
      "nomAdmin": "Fatou Diallo",
      "dateCreation": "2024-01-12T14:20:00",
      "nombreMembres": 3
    }
  ],
  "invitationsRecues": [
    {
      "id": 1,
      "nomFamille": "Famille Keita",
      "nomAdmin": "Moussa Keita",
      "codeInvitation": "ABC12345",
      "statut": "EN_ATTENTE",
      "dateCreation": "2024-01-15T10:30:00"
    },
    {
      "id": 2,
      "nomFamille": "Famille Coulibaly",
      "nomAdmin": "Aminata Coulibaly",
      "codeInvitation": "XYZ67890",
      "statut": "ACCEPTEE",
      "dateCreation": "2024-01-14T16:45:00"
    },
    {
      "id": 3,
      "nomFamille": "Famille Sangaré",
      "nomAdmin": "Boubacar Sangaré",
      "codeInvitation": "DEF54321",
      "statut": "REFUSEE",
      "dateCreation": "2024-01-13T11:15:00"
    }
  ]
}
```

---

## 🔧 **7. SUPER-ADMIN**

### **7.1 Dashboard Super-Admin Complet**
```http
GET /api/superadmin/dashboard
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "nombreUtilisateurs": 4,
  "nombreFamilles": 1,
  "nombreContes": 2,
  "nombreArtisanats": 1,
  "nombreProverbes": 1,
  "nombreDevinettes": 1,
  "nombreQuizPublics": 2,
  "contenusRecents": [
    {
      "id": 1,
      "titre": "Conte de la tortue et du lièvre",
      "typeContenu": "CONTE",
      "dateCreation": "2024-01-15T10:30:00",
      "nomCreateur": "Traoré",
      "prenomCreateur": "Amadou",
      "nomFamille": "Famille Traoré"
    },
    {
      "id": 2,
      "titre": "Poterie traditionnelle",
      "typeContenu": "ARTISANAT",
      "dateCreation": "2024-01-14T15:20:00",
      "nomCreateur": "Keita",
      "prenomCreateur": "Fatoumata",
      "nomFamille": "Famille Keita"
    }
  ],
  "famillesRecentes": [
    {
      "id": 1,
      "nom": "Famille Traoré",
      "description": "Famille traditionnelle du Mali",
      "ethnie": "Bambara",
      "region": "District de Bamako",
      "dateCreation": "2024-01-10T09:00:00",
      "nomAdmin": "Traoré",
      "prenomAdmin": "Amadou"
    },
    {
      "id": 2,
      "nom": "Famille Keita",
      "description": "Famille peule du Sénégal",
      "ethnie": "Peul",
      "region": "Région de Dakar",
      "dateCreation": "2024-01-12T14:20:00",
      "nomAdmin": "Keita",
      "prenomAdmin": "Moussa"
    }
  ]
}
```

### **7.2 Toutes les Familles de l'Application**
```http
GET /api/superadmin/dashboard/familles
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "nom": "Famille Traoré",
    "description": "Famille traditionnelle du Mali",
    "ethnie": "Bambara",
    "region": "District de Bamako",
    "dateCreation": "2024-01-10T09:00:00",
    "nomAdmin": "Traoré",
    "prenomAdmin": "Amadou",
    "emailAdmin": "amadou@example.com",
    "nombreMembres": 5
  },
  {
    "id": 2,
    "nom": "Famille Keita",
    "description": "Famille peule du Sénégal",
    "ethnie": "Peul",
    "region": "Région de Dakar",
    "dateCreation": "2024-01-12T14:20:00",
    "nomAdmin": "Keita",
    "prenomAdmin": "Moussa",
    "emailAdmin": "moussa@example.com",
    "nombreMembres": 3
  }
]
```

### **7.3 Quiz Publics Créés par le Super-Admin**
```http
GET /api/superadmin/dashboard/quiz-publics
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Quiz public sur l'histoire du Mali",
    "description": "Quiz ouvert à tous sur l'histoire du Mali",
    "typeQuiz": "PUBLIC",
    "statut": "ACTIF",
    "dateCreation": "2024-01-15T10:30:00",
    "nomCreateur": "Super Admin",
    "prenomCreateur": "Admin",
    "titreContenu": "Histoire du Mali",
    "nomFamille": "Famille Traoré"
  }
]
```

### **7.4 Tous les Contes de l'Application**
```http
GET /api/superadmin/dashboard/contes
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Conte de la tortue et du lièvre",
    "description": "Conte traditionnel bambara",
    "typeContenu": "CONTE",
    "statut": "PUBLIE",
    "dateCreation": "2024-01-10T09:00:00",
    "dateModification": "2024-01-10T09:00:00",
    "nomCreateur": "Traoré",
    "prenomCreateur": "Amadou",
    "emailCreateur": "amadou@example.com",
    "nomFamille": "Famille Traoré",
    "regionFamille": "District de Bamako",
    "urlFichier": "https://storage.example.com/contes/conte-tortue-lievre.pdf",
    "urlPhoto": "https://storage.example.com/photos/conte-tortue-lievre.jpg",
    "tailleFichier": 2048576,
    "duree": null,
    "lieu": "Bamako",
    "region": "District de Bamako",
    "dateEvenement": null,
    "idCategorie": 1,
    "nomCategorie": "Contes Traditionnels",
    "texteProverbe": null,
    "significationProverbe": null,
    "origineProverbe": null
  },
  {
    "id": 2,
    "titre": "Conte de la sagesse",
    "description": "Un conte sur la patience et la sagesse",
    "typeContenu": "CONTE",
    "statut": "BROUILLON",
    "dateCreation": "2024-01-12T14:20:00",
    "dateModification": "2024-01-12T14:20:00",
    "nomCreateur": "Keita",
    "prenomCreateur": "Fatoumata",
    "emailCreateur": "fatoumata@example.com",
    "nomFamille": "Famille Keita",
    "regionFamille": "Région de Dakar",
    "urlFichier": "https://storage.example.com/contes/conte-sagesse.pdf",
    "urlPhoto": "https://storage.example.com/photos/conte-sagesse.jpg",
    "tailleFichier": 1536000,
    "duree": null,
    "lieu": "Dakar",
    "region": "Région de Dakar",
    "dateEvenement": null,
    "idCategorie": 1,
    "nomCategorie": "Contes Traditionnels",
    "texteProverbe": null,
    "significationProverbe": null,
    "origineProverbe": null
  }
]
```

### **7.5 Tous les Artisanats de l'Application**
```http
GET /api/superadmin/dashboard/artisanats
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Poterie traditionnelle",
    "description": "Techniques de poterie ancestrale",
    "typeContenu": "ARTISANAT",
    "statut": "PUBLIE",
    "dateCreation": "2024-01-11T11:15:00",
    "dateModification": "2024-01-11T11:15:00",
    "nomCreateur": "Traoré",
    "prenomCreateur": "Amadou",
    "emailCreateur": "amadou@example.com",
    "nomFamille": "Famille Traoré",
    "regionFamille": "District de Bamako",
    "urlFichier": "https://storage.example.com/artisanats/poterie-traditionnelle.pdf",
    "urlPhoto": "https://storage.example.com/photos/poterie-traditionnelle.jpg",
    "tailleFichier": 3145728,
    "duree": null,
    "lieu": "Bamako",
    "region": "District de Bamako",
    "dateEvenement": null,
    "idCategorie": 2,
    "nomCategorie": "Artisanat",
    "texteProverbe": null,
    "significationProverbe": null,
    "origineProverbe": null
  }
]
```

### **7.6 Tous les Proverbes de l'Application**
```http
GET /api/superadmin/dashboard/proverbes
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Proverbe de la patience",
    "description": "La patience est une vertu",
    "typeContenu": "PROVERBE",
    "statut": "PUBLIE",
    "dateCreation": "2024-01-13T16:45:00",
    "dateModification": "2024-01-13T16:45:00",
    "nomCreateur": "Keita",
    "prenomCreateur": "Moussa",
    "emailCreateur": "moussa@example.com",
    "nomFamille": "Famille Keita",
    "regionFamille": "Région de Dakar",
    "urlFichier": null,
    "urlPhoto": "https://storage.example.com/photos/proverbe-patience.jpg",
    "tailleFichier": null,
    "duree": null,
    "lieu": "Dakar",
    "region": "Région de Dakar",
    "dateEvenement": null,
    "idCategorie": 3,
    "nomCategorie": "Proverbes",
    "texteProverbe": "La patience mène à tout",
    "significationProverbe": "Celui qui sait attendre finit toujours par atteindre son objectif",
    "origineProverbe": "Tradition Wolof"
  }
]
```

### **7.7 Toutes les Devinettes de l'Application**
```http
GET /api/superadmin/dashboard/devinettes
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Devinette du baobab",
    "description": "Qu'est-ce qui est grand et fort mais ne peut pas bouger ?",
    "typeContenu": "DEVINETTE",
    "statut": "PUBLIE",
    "dateCreation": "2024-01-14T08:30:00",
    "dateModification": "2024-01-14T08:30:00",
    "nomCreateur": "Traoré",
    "prenomCreateur": "Fatoumata",
    "emailCreateur": "fatoumata@example.com",
    "nomFamille": "Famille Traoré",
    "regionFamille": "District de Bamako",
    "urlFichier": null,
    "urlPhoto": "https://storage.example.com/photos/devinette-baobab.jpg",
    "tailleFichier": null,
    "duree": null,
    "lieu": "Bamako",
    "region": "District de Bamako",
    "dateEvenement": null,
    "idCategorie": 4,
    "nomCategorie": "Devinettes",
    "texteProverbe": null,
    "significationProverbe": null,
    "origineProverbe": null
  }
]
```

### **7.8 Tous les Utilisateurs de l'Application**
```http
GET /api/superadmin/dashboard/utilisateurs
Authorization: Bearer {token}
```

**Description :** Récupère tous les utilisateurs du système avec leurs informations détaillées.

**Réponse :**
```json
[
  {
    "id": 1,
    "nomComplet": "A.T. Amadou Traoré",
    "role": "ROLE_ADMIN",
    "telephone": "+223 76 12 34 56",
    "email": "amadou@example.com",
    "dateAjout": "2024-01-05T10:30:00",
    "actif": true
  },
  {
    "id": 2,
    "nomComplet": "F.K. Fatoumata Keita",
    "role": "ROLE_MEMBRE",
    "telephone": "+221 77 98 76 54",
    "email": "fatoumata@example.com",
    "dateAjout": "2024-01-08T14:20:00",
    "actif": true
  },
  {
    "id": 3,
    "nomComplet": "M.D. Moussa Diallo",
    "role": "ROLE_MEMBRE",
    "telephone": "+223 65 43 21 09",
    "email": "moussa@example.com",
    "dateAjout": "2024-01-12T09:15:00",
    "actif": false
  }
]
```

**Notes :**
- Le champ `nomComplet` est formaté avec les initiales du prénom et du nom suivies du nom complet : "P.N. Prenom Nom"
- Le champ `actif` indique si le compte utilisateur est actif ou désactivé
- Le `role` peut être `ROLE_ADMIN` (super-admin) ou `ROLE_MEMBRE` (utilisateur standard)

### **7.9 Activer/Désactiver un Utilisateur**
```http
PATCH /api/superadmin/dashboard/utilisateurs/{id}/activation?actif=true
Authorization: Bearer {token}
```

**Description :** Active ou désactive un utilisateur du système.

**Paramètres :**
- `id` (path) : ID de l'utilisateur
- `actif` (query param) : `true` pour activer, `false` pour désactiver

**Exemples :**

Activer un utilisateur :
```http
PATCH /api/superadmin/dashboard/utilisateurs/3/activation?actif=true
Authorization: Bearer {token}
```

Désactiver un utilisateur :
```http
PATCH /api/superadmin/dashboard/utilisateurs/3/activation?actif=false
Authorization: Bearer {token}
```

**Réponse :** 
- Code HTTP 204 (No Content) en cas de succès
- Pas de corps de réponse

**Note :** Un utilisateur désactivé ne pourra plus se connecter à l'application.

---

### **7.2 Valider/Rejeter Publication**
```http
POST /api/super-admin/demandes-publication/{demandeId}/valider
Authorization: Bearer {token}
```

```http
POST /api/super-admin/demandes-publication/{demandeId}/rejeter?commentaire=Contenu inapproprié
Authorization: Bearer {token}
```

---

## 🎯 **8. QUIZ ET ARBRE GÉNÉALOGIQUE**

### **8.1 Créer un Quiz pour un Conte (Famille)**
```http
POST /api/quiz-contenu/creer
Authorization: Bearer {token}
Content-Type: application/json

{
  "idContenu": 1,
  "titre": "Quiz sur le conte de la tortue et du lièvre",
  "description": "Testez vos connaissances sur ce conte traditionnel",
  "questions": [
    {
      "question": "Qui a gagné la course dans le conte ?",
      "typeReponse": "QCM",
      "propositions": [
        {
          "texte": "La tortue",
          "estCorrecte": true
        },
        {
          "texte": "Le lièvre",
          "estCorrecte": false
        },
        {
          "texte": "Personne",
          "estCorrecte": false
        }
      ]
    },
    {
      "question": "Le lièvre était-il confiant au début ?",
      "typeReponse": "VRAI_FAUX",
      "reponseVraiFaux": true
    }
  ]
}
```

**Réponse :**
```json
{
  "id": 1,
  "titre": "Quiz sur le conte de la tortue et du lièvre",
  "description": "Testez vos connaissances sur ce conte traditionnel",
  "typeQuiz": "FAMILIAL",
  "statut": "ACTIF",
  "idContenu": 1,
  "titreContenu": "Conte de la tortue et du lièvre"
}
```

**Règles :**
- ✅ **ADMIN famille** : Peut créer des quiz pour tous les contes de sa famille
- ✅ **EDITEUR famille** : Peut créer des quiz pour les contes qu'il a créés
- ❌ **LECTEUR famille** : Ne peut pas créer de quiz

### **8.2 Créer un Quiz pour un Conte Public (Super-Admin)**
```http
POST /api/quiz-contenu/creer-public
Authorization: Bearer {token}
Content-Type: application/json

{
  "idContenu": 5,
  "titre": "Quiz public sur le conte de la sagesse",
  "description": "Quiz ouvert à tous sur ce conte public",
  "questions": [
    {
      "question": "Quelle est la morale de ce conte ?",
      "typeReponse": "QCM",
      "propositions": [
        {
          "texte": "La patience est une vertu",
          "estCorrecte": true
        },
        {
          "texte": "La vitesse est importante",
          "estCorrecte": false
        }
      ]
    }
  ]
}
```

**Règles :**
- ✅ **SUPER-ADMIN uniquement** : Seul le super-admin peut créer des quiz pour les contenus publics
- ❌ **ADMIN famille** : Ne peut pas créer de quiz pour contenus publics

### **8.3 Voir les Contes d'une Famille avec leurs Quiz**
```http
GET /api/quiz-contenu/contes/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Conte de la tortue et du lièvre",
    "description": "Il était une fois une tortue et un lièvre...",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "BROUILLON",
    "urlFichier": "fichiers/conte_tortue_lièvre.pdf",
    "urlPhoto": "photos/tortue_lièvre.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré",
    "quiz": {
      "id": 1,
      "titre": "Quiz sur le conte de la tortue et du lièvre",
      "description": "Testez vos connaissances sur ce conte traditionnel",
      "typeQuiz": "FAMILIAL",
      "statut": "ACTIF",
      "idContenu": 1,
      "titreContenu": "Conte de la tortue et du lièvre"
    }
  }
]
```

### **8.4 Voir les Contes Publics avec leurs Quiz**
```http
GET /api/quiz-contenu/contes/publics
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 5,
    "titre": "Conte de la sagesse",
    "description": "Un conte sur la patience et la sagesse...",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "PUBLIE",
    "urlFichier": "fichiers/conte_sagesse.pdf",
    "urlPhoto": "photos/sagesse.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré",
    "quiz": {
      "id": 2,
      "titre": "Quiz public sur le conte de la sagesse",
      "description": "Quiz ouvert à tous sur ce conte public",
      "typeQuiz": "PUBLIC",
      "statut": "ACTIF",
      "idContenu": 5,
      "titreContenu": "Conte de la sagesse"
    }
  }
]
```

### **8.5 Répondre à un Quiz**
```http
POST /api/quiz-contenu/repondre
Authorization: Bearer {token}
Content-Type: application/json

{
  "idQuiz": 1,
  "reponses": [
    {
      "idQuestion": 1,
      "idProposition": 1
    },
    {
      "idQuestion": 2,
      "reponseVraiFaux": true
    }
  ]
}
```

**Réponse :**
```json
{
  "id": 1,
  "score": 2,
  "totalQuestions": 2,
  "datePassage": "2024-01-10T10:30:00",
  "idQuiz": 1,
  "titreQuiz": "Quiz sur le conte de la tortue et du lièvre",
  "idUtilisateur": 1,
  "nomUtilisateur": "Traoré",
  "prenomUtilisateur": "Amadou"
}
```

**Règles :**
- ✅ **Quiz familiaux** : Seuls les membres de la famille peuvent répondre
- ✅ **Quiz publics** : Tous les utilisateurs peuvent répondre

### **8.6 Voir son Score**
```http
GET /api/quiz-contenu/score
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idUtilisateur": 1,
  "nom": "Traoré",
  "prenom": "Amadou",
  "email": "amadou@example.com",
  "scoreTotal": 5,
  "nombreQuizReussis": 3,
  "nombreQuizFamiliaux": 2,
  "nombreQuizPublics": 1
}
```

### **8.7 Voir tous les Quiz d'une Famille avec Progression des Membres**
```http
GET /api/quiz-membre/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idFamille": 1,
  "nomFamille": "Famille Traoré",
  "descriptionFamille": "Famille traditionnelle du Mali",
  "nombreQuizTotal": 3,
  "nombreMembresAvecQuiz": 2,
  "scoreTotalFamille": 15,
  "membresQuiz": [
    {
      "idMembre": 1,
      "nomMembre": "Traoré",
      "prenomMembre": "Amadou",
      "emailMembre": "amadou@example.com",
      "roleMembre": "ADMIN",
      "lienParenteMembre": "Chef de famille",
      "nombreQuizTotal": 3,
      "nombreQuizTermines": 2,
      "nombreQuizEnCours": 1,
      "scoreTotalMembre": 8,
      "quizProgression": [
        {
          "idQuiz": 1,
          "titreQuiz": "Quiz sur le conte de la tortue et du lièvre",
          "descriptionQuiz": "Testez vos connaissances sur ce conte traditionnel",
          "nomConte": "Conte de la tortue et du lièvre",
          "descriptionConte": "Il était une fois une tortue et un lièvre...",
          "nomAuteurConte": "Traoré",
          "prenomAuteurConte": "Amadou",
          "nombreQuestionsTotal": 3,
          "nombreQuestionsRepondues": 3,
          "scoreActuel": 3,
          "dateDerniereReponse": "2024-01-10T10:30:00",
          "statutQuiz": "TERMINE",
          "typeQuiz": "FAMILIAL",
          "idContenu": 1,
          "idFamille": 1,
          "nomFamille": "Famille Traoré"
        }
      ]
    }
  ]
}
```

### **8.8 Voir les Quiz d'un Membre Spécifique**
```http
GET /api/quiz-membre/famille/{familleId}/membre/{membreId}
Authorization: Bearer {token}
```

### **8.9 Voir l'Arbre Généalogique d'une Famille**
```http
GET /api/arbre-genealogique/famille/{familleId}
Authorization: Bearer {token}
```

**Note :** Les membres sont affichés du plus grand au plus petit selon l'âge (triés par date de naissance croissante).

**Réponse :**
```json
{
  "id": 1,
  "nom": "Arbre généalogique de Famille Traoré",
  "description": "Arbre généalogique de la famille Famille Traoré",
  "dateCreation": "2024-01-10T09:00:00",
  "nombreMembres": 3,
  "idFamille": 1,
  "nomFamille": "Famille Traoré",
  "membres": [
    {
      "id": 1,
      "nomComplet": "Amadou Traoré",
      "dateNaissance": "1950-01-15",
      "lieuNaissance": "Bamako, Mali",
      "relationFamille": "Chef de famille",
      "telephone": "+223 70 12 34 56",
      "email": "amadou@example.com",
      "biographie": "Fondateur de la famille Traoré, ancien chef de village",
      "urlPhoto": "photos/amadou_traore.jpg",
      "nomParent1": null,
      "nomParent2": null,
      "nomConjoint": "Fatoumata Traoré",
      "dateAjout": "2024-01-10T09:00:00",
      "idFamille": 1,
      "nomFamille": "Famille Traoré"
    },
    {
      "id": 2,
      "nomComplet": "Fatoumata Traoré",
      "dateNaissance": "1955-03-20",
      "lieuNaissance": "Ségou, Mali",
      "relationFamille": "Épouse",
      "telephone": "+223 70 12 34 57",
      "email": "fatoumata@example.com",
      "biographie": "Épouse d'Amadou, mère de 5 enfants",
      "urlPhoto": "photos/fatoumata_traore.jpg",
      "nomParent1": "Moussa Traoré",
      "nomParent2": "Aminata Traoré",
      "nomConjoint": "Amadou Traoré",
      "dateAjout": "2024-01-10T09:15:00",
      "idFamille": 1,
      "nomFamille": "Famille Traoré"
    }
  ]
}
```

### **8.10 Ajouter un Membre à l'Arbre Généalogique**
```http
POST /api/arbre-genealogique/ajouter-membre
Authorization: Bearer {token}
Content-Type: multipart/form-data

{
  "idFamille": 1,
  "nomComplet": "Ibrahim Traoré",
  "dateNaissance": "1980-06-10",
  "lieuNaissance": "Bamako, Mali",
  "relationFamille": "Fils",
  "telephone": "+223 70 12 34 58",
  "email": "ibrahim@example.com",
  "biographie": "Fils aîné d'Amadou et Fatoumata, ingénieur",
  "photo": [fichier photo],
  "idParent1": 1,
  "idParent2": 2,
  "idConjoint": null
}
```

**Réponse :**
```json
{
  "id": 3,
  "nomComplet": "Ibrahim Traoré",
  "dateNaissance": "1980-06-10",
  "lieuNaissance": "Bamako, Mali",
  "relationFamille": "Fils",
  "telephone": "+223 70 12 34 58",
  "email": "ibrahim@example.com",
  "biographie": "Fils aîné d'Amadou et Fatoumata, ingénieur",
  "urlPhoto": "photos/ibrahim_traore.jpg",
  "nomParent1": "Amadou Traoré",
  "nomParent2": "Fatoumata Traoré",
  "nomConjoint": null,
  "dateAjout": "2024-01-10T10:00:00",
  "idFamille": 1,
  "nomFamille": "Famille Traoré"
}
```

### **8.11 Voir un Membre Spécifique de l'Arbre**
```http
GET /api/arbre-genealogique/membre/{membreId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "id": 1,
  "nomComplet": "Amadou Traoré",
  "dateNaissance": "1950-01-15",
  "lieuNaissance": "Bamako, Mali",
  "relationFamille": "Chef de famille",
  "telephone": "+223 70 12 34 56",
  "email": "amadou@example.com",
  "biographie": "Fondateur de la famille Traoré, ancien chef de village",
  "urlPhoto": "photos/amadou_traore.jpg",
  "nomParent1": null,
  "nomParent2": null,
  "nomConjoint": "Fatoumata Traoré",
  "dateAjout": "2024-01-10T09:00:00",
  "idFamille": 1,
  "nomFamille": "Famille Traoré"
}
```

---

## 📝 **NOUVELLES FONCTIONNALITÉS AJOUTÉES**

### **Champs Supplémentaires**
- **Utilisateur** : `numeroTelephone`, `ethnie`
- **Famille** : `ethnie`
- **Contenu** : `region`

### **Workflow d'Invitation Corrigé**
1. **Inscription avec code** → Compte créé + Membre famille + Invitation EN_ATTENTE
2. **Dashboard personnel** → Voir invitations en attente
3. **Accepter/Refuser** → Statut final de l'invitation

### **Services de Traduction**
- **MyMemory API** : Traduction multilingue
- **Service Bambara** : Traduction spécialisée avec dictionnaire
- **Service Intelligent** : Choix automatique du meilleur service

### **Système de Quiz Amélioré**
- **Quiz familiaux** : Créés par ADMIN/EDITEUR pour les contes de la famille
- **Quiz publics** : Créés par SUPER-ADMIN pour les contes publics
- **Types de questions** : QCM et Vrai/Faux
- **Système de score** : Points gagnés pour les bonnes réponses
- **Contes avec quiz** : Affichage des contes avec leurs quiz éventuels

---

## 🎯 **QUIZ ET CONTENUS**

### **8.7 Créer un Quiz pour un Conte (Famille)**
```http
POST /api/quiz-contenu/creer
Authorization: Bearer {token}
Content-Type: application/json

{
  "idContenu": 1,
  "titre": "Quiz sur le conte de la tortue et du lièvre",
  "description": "Testez vos connaissances sur ce conte traditionnel",
  "questions": [
    {
      "question": "Qui a gagné la course dans le conte ?",
      "typeReponse": "QCM",
      "propositions": [
        {
          "texte": "La tortue",
          "estCorrecte": true
        },
        {
          "texte": "Le lièvre",
          "estCorrecte": false
        },
        {
          "texte": "Personne",
          "estCorrecte": false
        }
      ]
    },
    {
      "question": "Le lièvre était-il confiant au début ?",
      "typeReponse": "VRAI_FAUX",
      "reponseVraiFaux": true
    }
  ]
}
```

**Réponse :**
```json
{
  "id": 1,
  "titre": "Quiz sur le conte de la tortue et du lièvre",
  "description": "Testez vos connaissances sur ce conte traditionnel",
  "typeQuiz": "FAMILIAL",
  "statut": "ACTIF",
  "idContenu": 1,
  "titreContenu": "Conte de la tortue et du lièvre"
}
```

### **8.8 Créer un Quiz pour un Conte Public (Super-Admin)**
```http
POST /api/quiz-contenu/creer-public
Authorization: Bearer {token}
Content-Type: application/json

{
  "idContenu": 5,
  "titre": "Quiz public sur le conte de la sagesse",
  "description": "Quiz ouvert à tous sur ce conte public",
  "questions": [
    {
      "question": "Quelle est la morale de ce conte ?",
      "typeReponse": "QCM",
      "propositions": [
        {
          "texte": "La patience est une vertu",
          "estCorrecte": true
        },
        {
          "texte": "La vitesse est importante",
          "estCorrecte": false
        }
      ]
    }
  ]
}
```

### **8.9 Voir les Contes d'une Famille avec leurs Quiz**
```http
GET /api/quiz-contenu/contes/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Conte de la tortue et du lièvre",
    "description": "Il était une fois une tortue et un lièvre...",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "BROUILLON",
    "urlFichier": "fichiers/conte_tortue_lièvre.pdf",
    "urlPhoto": "photos/tortue_lièvre.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré",
    "quiz": {
      "id": 1,
      "titre": "Quiz sur le conte de la tortue et du lièvre",
      "description": "Testez vos connaissances sur ce conte traditionnel",
      "typeQuiz": "FAMILIAL",
      "statut": "ACTIF",
      "idContenu": 1,
      "titreContenu": "Conte de la tortue et du lièvre"
    }
  }
]
```

### **8.10 Voir les Contes Publics avec leurs Quiz**
```http
GET /api/quiz-contenu/contes/publics
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 5,
    "titre": "Conte de la sagesse",
    "description": "Un conte sur la patience et la sagesse...",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "PUBLIE",
    "urlFichier": "fichiers/conte_sagesse.pdf",
    "urlPhoto": "photos/sagesse.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré",
    "quiz": {
      "id": 2,
      "titre": "Quiz public sur le conte de la sagesse",
      "description": "Quiz ouvert à tous sur ce conte public",
      "typeQuiz": "PUBLIC",
      "statut": "ACTIF",
      "idContenu": 5,
      "titreContenu": "Conte de la sagesse"
    }
  }
]
```

### **8.11 Répondre à un Quiz**
```http
POST /api/quiz-contenu/repondre
Authorization: Bearer {token}
Content-Type: application/json

{
  "idQuiz": 1,
  "reponses": [
    {
      "idQuestion": 1,
      "idProposition": 1
    },
    {
      "idQuestion": 2,
      "reponseVraiFaux": true
    }
  ]
}
```

**Réponse :**
```json
{
  "id": 1,
  "score": 2,
  "totalQuestions": 2,
  "datePassage": "2024-01-10T10:30:00",
  "idQuiz": 1,
  "titreQuiz": "Quiz sur le conte de la tortue et du lièvre",
  "idUtilisateur": 1,
  "nomUtilisateur": "Traoré",
  "prenomUtilisateur": "Amadou"
}
```

### **8.12 Voir son Score**
```http
GET /api/quiz-contenu/score
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idUtilisateur": 1,
  "nom": "Traoré",
  "prenom": "Amadou",
  "email": "amadou@example.com",
  "scoreTotal": 5,
  "nombreQuizReussis": 3,
  "nombreQuizFamiliaux": 2,
  "nombreQuizPublics": 1
}
```

### **8.13 Voir tous les Quiz d'une Famille avec Progression des Membres**
```http
GET /api/quiz-membre/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idFamille": 1,
  "nomFamille": "Famille Traoré",
  "descriptionFamille": "Famille traditionnelle du Mali",
  "nombreQuizTotal": 3,
  "nombreMembresAvecQuiz": 2,
  "scoreTotalFamille": 15,
  "membresQuiz": [
    {
      "idMembre": 1,
      "nomMembre": "Traoré",
      "prenomMembre": "Amadou",
      "emailMembre": "amadou@example.com",
      "roleMembre": "ADMIN",
      "lienParenteMembre": "Chef de famille",
      "nombreQuizTotal": 3,
      "nombreQuizTermines": 2,
      "nombreQuizEnCours": 1,
      "scoreTotalMembre": 8,
      "quizProgression": [
        {
          "idQuiz": 1,
          "titreQuiz": "Quiz sur le conte de la tortue et du lièvre",
          "descriptionQuiz": "Testez vos connaissances sur ce conte traditionnel",
          "nomConte": "Conte de la tortue et du lièvre",
          "descriptionConte": "Il était une fois une tortue et un lièvre...",
          "nomAuteurConte": "Traoré",
          "prenomAuteurConte": "Amadou",
          "nombreQuestionsTotal": 3,
          "nombreQuestionsRepondues": 3,
          "scoreActuel": 3,
          "dateDerniereReponse": "2024-01-10T10:30:00",
          "statutQuiz": "TERMINE",
          "typeQuiz": "FAMILIAL",
          "idContenu": 1,
          "idFamille": 1,
          "nomFamille": "Famille Traoré"
        },
        {
          "idQuiz": 2,
          "titreQuiz": "Quiz sur le conte de la sagesse",
          "descriptionQuiz": "Quiz sur la sagesse traditionnelle",
          "nomConte": "Conte de la sagesse",
          "descriptionConte": "Un conte sur la patience et la sagesse...",
          "nomAuteurConte": "Traoré",
          "prenomAuteurConte": "Amadou",
          "nombreQuestionsTotal": 2,
          "nombreQuestionsRepondues": 1,
          "scoreActuel": 1,
          "dateDerniereReponse": "2024-01-10T11:00:00",
          "statutQuiz": "EN_COURS",
          "typeQuiz": "FAMILIAL",
          "idContenu": 2,
          "idFamille": 1,
          "nomFamille": "Famille Traoré"
        },
        {
          "idQuiz": 3,
          "titreQuiz": "Quiz sur le conte de la patience",
          "descriptionQuiz": "Quiz sur la patience et la persévérance",
          "nomConte": "Conte de la patience",
          "descriptionConte": "Un conte sur l'importance de la patience...",
          "nomAuteurConte": "Traoré",
          "prenomAuteurConte": "Amadou",
          "nombreQuestionsTotal": 4,
          "nombreQuestionsRepondues": 0,
          "scoreActuel": 0,
          "dateDerniereReponse": null,
          "statutQuiz": "NON_COMMENCE",
          "typeQuiz": "FAMILIAL",
          "idContenu": 3,
          "idFamille": 1,
          "nomFamille": "Famille Traoré"
        }
      ]
    },
    {
      "idMembre": 2,
      "nomMembre": "Traoré",
      "prenomMembre": "Fatoumata",
      "emailMembre": "fatoumata@example.com",
      "roleMembre": "EDITEUR",
      "lienParenteMembre": "Fille",
      "nombreQuizTotal": 3,
      "nombreQuizTermines": 1,
      "nombreQuizEnCours": 0,
      "scoreTotalMembre": 7,
      "quizProgression": [
        {
          "idQuiz": 1,
          "titreQuiz": "Quiz sur le conte de la tortue et du lièvre",
          "descriptionQuiz": "Testez vos connaissances sur ce conte traditionnel",
          "nomConte": "Conte de la tortue et du lièvre",
          "descriptionConte": "Il était une fois une tortue et un lièvre...",
          "nomAuteurConte": "Traoré",
          "prenomAuteurConte": "Amadou",
          "nombreQuestionsTotal": 3,
          "nombreQuestionsRepondues": 3,
          "scoreActuel": 2,
          "dateDerniereReponse": "2024-01-10T09:45:00",
          "statutQuiz": "TERMINE",
          "typeQuiz": "FAMILIAL",
          "idContenu": 1,
          "idFamille": 1,
          "nomFamille": "Famille Traoré"
        },
        {
          "idQuiz": 2,
          "titreQuiz": "Quiz sur le conte de la sagesse",
          "descriptionQuiz": "Quiz sur la sagesse traditionnelle",
          "nomConte": "Conte de la sagesse",
          "descriptionConte": "Un conte sur la patience et la sagesse...",
          "nomAuteurConte": "Traoré",
          "prenomAuteurConte": "Amadou",
          "nombreQuestionsTotal": 2,
          "nombreQuestionsRepondues": 0,
          "scoreActuel": 0,
          "dateDerniereReponse": null,
          "statutQuiz": "NON_COMMENCE",
          "typeQuiz": "FAMILIAL",
          "idContenu": 2,
          "idFamille": 1,
          "nomFamille": "Famille Traoré"
        },
        {
          "idQuiz": 3,
          "titreQuiz": "Quiz sur le conte de la patience",
          "descriptionQuiz": "Quiz sur la patience et la persévérance",
          "nomConte": "Conte de la patience",
          "descriptionConte": "Un conte sur l'importance de la patience...",
          "nomAuteurConte": "Traoré",
          "prenomAuteurConte": "Amadou",
          "nombreQuestionsTotal": 4,
          "nombreQuestionsRepondues": 0,
          "scoreActuel": 0,
          "dateDerniereReponse": null,
          "statutQuiz": "NON_COMMENCE",
          "typeQuiz": "FAMILIAL",
          "idContenu": 3,
          "idFamille": 1,
          "nomFamille": "Famille Traoré"
        }
      ]
    }
  ]
}
```

### **8.14 Voir les Quiz d'un Membre Spécifique**
```http
GET /api/quiz-membre/famille/{familleId}/membre/{membreId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idMembre": 1,
  "nomMembre": "Traoré",
  "prenomMembre": "Amadou",
  "emailMembre": "amadou@example.com",
  "roleMembre": "ADMIN",
  "lienParenteMembre": "Chef de famille",
  "nombreQuizTotal": 3,
  "nombreQuizTermines": 2,
  "nombreQuizEnCours": 1,
  "scoreTotalMembre": 8,
  "quizProgression": [
    {
      "idQuiz": 1,
      "titreQuiz": "Quiz sur le conte de la tortue et du lièvre",
      "descriptionQuiz": "Testez vos connaissances sur ce conte traditionnel",
      "nomConte": "Conte de la tortue et du lièvre",
      "descriptionConte": "Il était une fois une tortue et un lièvre...",
      "nomAuteurConte": "Traoré",
      "prenomAuteurConte": "Amadou",
      "nombreQuestionsTotal": 3,
      "nombreQuestionsRepondues": 3,
      "scoreActuel": 3,
      "dateDerniereReponse": "2024-01-10T10:30:00",
      "statutQuiz": "TERMINE",
      "typeQuiz": "FAMILIAL",
      "idContenu": 1,
      "idFamille": 1,
      "nomFamille": "Famille Traoré"
    },
    {
      "idQuiz": 2,
      "titreQuiz": "Quiz sur le conte de la sagesse",
      "descriptionQuiz": "Quiz sur la sagesse traditionnelle",
      "nomConte": "Conte de la sagesse",
      "descriptionConte": "Un conte sur la patience et la sagesse...",
      "nomAuteurConte": "Traoré",
      "prenomAuteurConte": "Amadou",
      "nombreQuestionsTotal": 2,
      "nombreQuestionsRepondues": 1,
      "scoreActuel": 1,
      "dateDerniereReponse": "2024-01-10T11:00:00",
      "statutQuiz": "EN_COURS",
      "typeQuiz": "FAMILIAL",
      "idContenu": 2,
      "idFamille": 1,
      "nomFamille": "Famille Traoré"
    },
    {
      "idQuiz": 3,
      "titreQuiz": "Quiz sur le conte de la patience",
      "descriptionQuiz": "Quiz sur la patience et la persévérance",
      "nomConte": "Conte de la patience",
      "descriptionConte": "Un conte sur l'importance de la patience...",
      "nomAuteurConte": "Traoré",
      "prenomAuteurConte": "Amadou",
      "nombreQuestionsTotal": 4,
      "nombreQuestionsRepondues": 0,
      "scoreActuel": 0,
      "dateDerniereReponse": null,
      "statutQuiz": "NON_COMMENCE",
      "typeQuiz": "FAMILIAL",
      "idContenu": 3,
      "idFamille": 1,
      "nomFamille": "Famille Traoré"
    }
  ]
}
```

---

## 📚 **AFFICHAGE DES CONTENUS DÉTAILLÉS**

### **9.1 Voir un Conte avec son Contenu Détaillé**
```http
GET /api/conte-detail/{conteId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "id": 1,
  "titre": "Conte de la tortue et du lièvre",
  "description": "Il était une fois une tortue et un lièvre...",
  "nomAuteur": "Traoré",
  "prenomAuteur": "Amadou",
  "emailAuteur": "amadou@example.com",
  "roleAuteur": "ADMIN",
  "lienParenteAuteur": "Chef de famille",
  "dateCreation": "2024-01-10T09:00:00",
  "statut": "BROUILLON",
  "urlFichier": "fichiers/conte_tortue_lièvre.pdf",
  "urlPhoto": "photos/tortue_lièvre.jpg",
  "lieu": "Bamako",
  "region": "District de Bamako",
  "idFamille": 1,
  "nomFamille": "Famille Traoré",
  "contenuTexte": "Il était une fois une tortue et un lièvre qui décidèrent de faire une course. Le lièvre était très confiant et se moquait de la tortue. 'Tu es si lente !' disait-il. 'Je vais gagner facilement !' La tortue répondit calmement : 'Peut-être, mais la persévérance peut surmonter la vitesse.' La course commença et le lièvre partit en courant, laissant la tortue loin derrière. Confiant de sa victoire, le lièvre s'arrêta pour faire une sieste. Pendant ce temps, la tortue continua lentement mais sûrement. Quand le lièvre se réveilla, il vit la tortue franchir la ligne d'arrivée. La morale de cette histoire est que la persévérance et la détermination peuvent triompher de la vitesse et de l'arrogance.",
  "typeFichier": "PDF",
  "nombreMots": 89,
  "nombreCaracteres": 567,
  "nombreLignes": 8
}
```

### **9.2 Voir tous les Contes d'une Famille avec leur Contenu**
```http
GET /api/conte-detail/famille/{familleId}
Authorization: Bearer {token}
```

**Réponse :**
```json
[
  {
    "id": 1,
    "titre": "Conte de la tortue et du lièvre",
    "description": "Il était une fois une tortue et un lièvre...",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "roleAuteur": "ADMIN",
    "lienParenteAuteur": "Chef de famille",
    "dateCreation": "2024-01-10T09:00:00",
    "statut": "BROUILLON",
    "urlFichier": "fichiers/conte_tortue_lièvre.pdf",
    "urlPhoto": "photos/tortue_lièvre.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré",
    "contenuTexte": "Il était une fois une tortue et un lièvre qui décidèrent de faire une course. Le lièvre était très confiant et se moquait de la tortue. 'Tu es si lente !' disait-il. 'Je vais gagner facilement !' La tortue répondit calmement : 'Peut-être, mais la persévérance peut surmonter la vitesse.' La course commença et le lièvre partit en courant, laissant la tortue loin derrière. Confiant de sa victoire, le lièvre s'arrêta pour faire une sieste. Pendant ce temps, la tortue continua lentement mais sûrement. Quand le lièvre se réveilla, il vit la tortue franchir la ligne d'arrivée. La morale de cette histoire est que la persévérance et la détermination peuvent triompher de la vitesse et de l'arrogance.",
    "typeFichier": "PDF",
    "nombreMots": 89,
    "nombreCaracteres": 567,
    "nombreLignes": 8
  },
  {
    "id": 2,
    "titre": "Conte de la sagesse",
    "description": "Un conte sur la patience et la sagesse...",
    "nomAuteur": "Traoré",
    "prenomAuteur": "Amadou",
    "emailAuteur": "amadou@example.com",
    "roleAuteur": "ADMIN",
    "lienParenteAuteur": "Chef de famille",
    "dateCreation": "2024-01-10T10:00:00",
    "statut": "PUBLIE",
    "urlFichier": "fichiers/conte_sagesse.txt",
    "urlPhoto": "photos/sagesse.jpg",
    "lieu": "Bamako",
    "region": "District de Bamako",
    "idFamille": 1,
    "nomFamille": "Famille Traoré",
    "contenuTexte": "Dans un village lointain, vivait un vieux sage qui était connu pour sa patience infinie. Un jour, un jeune homme impatient vint le voir et lui demanda : 'Comment puis-je devenir aussi sage que vous ?' Le sage répondit : 'La sagesse vient avec le temps et la patience. Apprends à écouter avant de parler, à observer avant d'agir, et à réfléchir avant de juger.' Le jeune homme était déçu par cette réponse simple et partit. Mais au fil des années, il comprit que la sagesse du vieil homme était dans sa simplicité. Il revint voir le sage et lui dit : 'Maintenant je comprends. La sagesse n'est pas dans la complexité, mais dans la simplicité et la patience.' Le sage sourit et répondit : 'Tu as appris la leçon la plus importante : la sagesse vient de l'expérience et de la patience.'",
    "typeFichier": "TXT",
    "nombreMots": 156,
    "nombreCaracteres": 987,
    "nombreLignes": 12
  }
]
```

---

## 🌍 **TRADUCTION DES CONTENUS AVEC HUGGINGFACE NLLB-200**

### **10.1 Traduire un Conte Complet**
```http
GET /api/traduction/conte/{conteId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idConte": 1,
  "titreOriginal": "Conte de la tortue et du lièvre",
  "contenuOriginal": "Il était une fois une tortue et un lièvre qui décidèrent de faire une course...",
  "traductionsTitre": {
    "francais": "Conte de la tortue et du lièvre",
    "bambara": "Kɔrɔ ni jiri kan",
    "anglais": "Tale of the tortoise and the hare"
  },
  "traductionsContenu": {
    "francais": "Il était une fois une tortue et un lièvre qui décidèrent de faire une course...",
    "bambara": "A ka kɛ ni kɔrɔ ni jiri kan ka kɛ ka kɛ ka kɛ...",
    "anglais": "Once upon a time, a tortoise and a hare decided to have a race..."
  },
  "traductionsCompletes": {
    "francais": "Titre: Conte de la tortue et du lièvre\n\nContenu:\nIl était une fois une tortue et un lièvre qui décidèrent de faire une course...",
    "bambara": "Titre: Kɔrɔ ni jiri kan\n\nContenu:\nA ka kɛ ni kɔrɔ ni jiri kan ka kɛ ka kɛ ka kɛ...",
    "anglais": "Titre: Tale of the tortoise and the hare\n\nContenu:\nOnce upon a time, a tortoise and a hare decided to have a race..."
  },
  "langueSource": "francais",
  "statutTraduction": "SUCCES"
}
```

### **10.2 Traduire un Artisanat Complet**
```http
GET /api/traduction/artisanat/{artisanatId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idConte": 2,
  "titreOriginal": "Poterie traditionnelle",
  "contenuOriginal": "Technique de fabrication de pots en argile...",
  "traductionsTitre": {
    "francais": "Poterie traditionnelle",
    "bambara": "Kɔrɔ ni jiri kan",
    "anglais": "Traditional pottery"
  },
  "traductionsContenu": {
    "francais": "Technique de fabrication de pots en argile...",
    "bambara": "Kɔrɔ ni jiri kan ka kɛ ka kɛ...",
    "anglais": "Clay pot making technique..."
  },
  "traductionsCompletes": {
    "francais": "Titre: Poterie traditionnelle\n\nDescription:\nTechnique de fabrication de pots en argile...",
    "bambara": "Titre: Kɔrɔ ni jiri kan\n\nDescription:\nKɔrɔ ni jiri kan ka kɛ ka kɛ...",
    "anglais": "Titre: Traditional pottery\n\nDescription:\nClay pot making technique..."
  },
  "langueSource": "francais",
  "statutTraduction": "SUCCES"
}
```

### **10.3 Traduire un Proverbe Complet**
```http
GET /api/traduction/proverbe/{proverbeId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idConte": 3,
  "titreOriginal": "Proverbe de la patience",
  "contenuOriginal": "La patience est une vertu",
  "traductionsTitre": {
    "francais": "Proverbe de la patience",
    "bambara": "Kɔrɔ ni jiri kan",
    "anglais": "Patience proverb"
  },
  "traductionsContenu": {
    "francais": "Titre: Proverbe de la patience\n\nProverbe: La patience est une vertu\n\nSignification: Il faut être patient\n\nOrigine: Mali",
    "bambara": "Titre: Kɔrɔ ni jiri kan\n\nProverbe: Kɔrɔ ni jiri kan ka kɛ\n\nSignification: Kɔrɔ ni jiri kan ka kɛ\n\nOrigine: Mali",
    "anglais": "Titre: Patience proverb\n\nProverb: Patience is a virtue\n\nMeaning: One must be patient\n\nOrigin: Mali"
  },
  "traductionsCompletes": {
    "francais": "Titre: Proverbe de la patience\n\nProverbe: La patience est une vertu\n\nSignification: Il faut être patient\n\nOrigine: Mali",
    "bambara": "Titre: Kɔrɔ ni jiri kan\n\nProverbe: Kɔrɔ ni jiri kan ka kɛ\n\nSignification: Kɔrɔ ni jiri kan ka kɛ\n\nOrigine: Mali",
    "anglais": "Titre: Patience proverb\n\nProverb: Patience is a virtue\n\nMeaning: One must be patient\n\nOrigin: Mali"
  },
  "langueSource": "francais",
  "statutTraduction": "SUCCES"
}
```

### **10.4 Traduire une Devinette Complète**
```http
GET /api/traduction/devinette/{devinetteId}
Authorization: Bearer {token}
```

**Réponse :**
```json
{
  "idConte": 4,
  "titreOriginal": "Devinette de la lune",
  "contenuOriginal": "Qu'est-ce qui brille la nuit ?",
  "traductionsTitre": {
    "francais": "Devinette de la lune",
    "bambara": "Kɔrɔ ni jiri kan",
    "anglais": "Moon riddle"
  },
  "traductionsContenu": {
    "francais": "Titre: Devinette de la lune\n\nDevinette: Qu'est-ce qui brille la nuit ?\n\nRéponse: La lune",
    "bambara": "Titre: Kɔrɔ ni jiri kan\n\nDevinette: Kɔrɔ ni jiri kan ka kɛ ?\n\nRéponse: Kɔrɔ ni jiri kan",
    "anglais": "Titre: Moon riddle\n\nRiddle: What shines at night?\n\nAnswer: The moon"
  },
  "traductionsCompletes": {
    "francais": "Titre: Devinette de la lune\n\nDevinette: Qu'est-ce qui brille la nuit ?\n\nRéponse: La lune",
    "bambara": "Titre: Kɔrɔ ni jiri kan\n\nDevinette: Kɔrɔ ni jiri kan ka kɛ ?\n\nRéponse: Kɔrɔ ni jiri kan",
    "anglais": "Titre: Moon riddle\n\nRiddle: What shines at night?\n\nAnswer: The moon"
  },
  "langueSource": "francais",
  "statutTraduction": "SUCCES"
}
```

---

## 🚀 **GUIDE D'UTILISATION**

### **Pour un Nouvel Utilisateur :**
1. S'inscrire avec ou sans code d'invitation
2. Se connecter
3. Si invitation → Accepter/refuser depuis le dashboard
4. Créer ou rejoindre une famille

### **Pour un Admin de Famille :**
1. Créer une famille
2. Inviter des membres
3. Créer du contenu
4. Demander publication publique

### **Pour un Super-Admin :**
1. Voir les statistiques globales
2. Valider les demandes de publication
3. Gérer les catégories de contenu

---

**📋 Total : 37 endpoints organisés selon votre workflow complet !**