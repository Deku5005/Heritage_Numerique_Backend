# Endpoint Utilisateur avec Familles et Rôles

## 📋 Vue d'ensemble

Cette documentation décrit les endpoints pour récupérer les informations complètes d'un utilisateur, incluant ses appartenances aux familles avec leurs rôles respectifs.

## 🎯 Fonctionnalités

### 1. Récupération d'un utilisateur par ID (avec toutes ses familles)
**Endpoint** : `GET /api/utilisateurs/{id}`

**Description** : Retourne toutes les informations d'un utilisateur (sauf le mot de passe) incluant ses familles avec les rôles.

**Paramètres** :
- `id` (Path) : ID de l'utilisateur

**Exemple de requête** :
```http
GET /api/utilisateurs/1
```

**Réponse (200 OK)** :
```json
{
  "id": 1,
  "nom": "Diallo",
  "prenom": "Mamadou",
  "email": "mamadou.diallo@example.com",
  "numeroTelephone": "+221 77 123 45 67",
  "ethnie": "Peul",
  "role": "ROLE_MEMBRE",
  "actif": true,
  "dateCreation": "2024-10-15T10:30:00",
  "familles": [
    {
      "idFamille": 1,
      "nomFamille": "Famille Diallo",
      "descriptionFamille": "Famille traditionnelle peule du Fouta Djallon",
      "ethnie": "Peul",
      "region": "Fouta Djallon",
      "roleFamille": "ADMIN",
      "lienParente": "Chef de famille",
      "dateAjout": "2024-10-15T10:35:00"
    },
    {
      "idFamille": 3,
      "nomFamille": "Famille Bah",
      "descriptionFamille": "Famille étendue Bah",
      "ethnie": "Peul",
      "region": "Labé",
      "roleFamille": "LECTEUR",
      "lienParente": "Gendre",
      "dateAjout": "2024-11-01T14:20:00"
    }
  ]
}
```

### 2. Récupération d'un utilisateur par email (avec toutes ses familles)
**Endpoint** : `GET /api/utilisateurs/email/{email}`

**Description** : Retourne toutes les informations d'un utilisateur (sauf le mot de passe) incluant ses familles avec les rôles.

**Paramètres** :
- `email` (Path) : Email de l'utilisateur

**Exemple de requête** :
```http
GET /api/utilisateurs/email/mamadou.diallo@example.com
```

**Réponse** : Même structure que l'endpoint par ID

---

### 3. ⭐ Récupération d'un utilisateur avec son rôle dans UNE famille spécifique
**Endpoint** : `GET /api/utilisateurs/{utilisateurId}/famille/{familleId}`

**Description** : Retourne toutes les informations d'un utilisateur (sauf le mot de passe) avec son rôle spécifique dans une famille donnée.

**Paramètres** :
- `utilisateurId` (Path) : ID de l'utilisateur
- `familleId` (Path) : ID de la famille

**Exemple de requête** :
```http
GET /api/utilisateurs/1/famille/3
```

**Réponse (200 OK)** :
```json
{
  "id": 1,
  "nom": "Diallo",
  "prenom": "Mamadou",
  "email": "mamadou.diallo@example.com",
  "numeroTelephone": "+221 77 123 45 67",
  "ethnie": "Peul",
  "role": "ROLE_MEMBRE",
  "actif": true,
  "dateCreation": "2024-10-15T10:30:00",
  "idFamille": 3,
  "nomFamille": "Famille Bah",
  "roleFamille": "LECTEUR",
  "lienParente": "Gendre",
  "dateAjoutFamille": "2024-11-01T14:20:00"
}
```

**Cas d'erreur (404)** :
- L'utilisateur n'existe pas
- La famille n'existe pas
- L'utilisateur n'est pas membre de cette famille

## 📊 Structure des données

### UtilisateurDTO (pour endpoints 1 et 2)
| Champ | Type | Description |
|-------|------|-------------|
| `id` | Long | Identifiant unique de l'utilisateur |
| `nom` | String | Nom de famille |
| `prenom` | String | Prénom |
| `email` | String | Adresse email (unique) |
| `numeroTelephone` | String | Numéro de téléphone |
| `ethnie` | String | Ethnie de l'utilisateur |
| `role` | String | Rôle global (ROLE_ADMIN, ROLE_MEMBRE) |
| `actif` | Boolean | Statut actif/inactif |
| `dateCreation` | LocalDateTime | Date de création du compte |
| `familles` | List<FamilleUtilisateurDTO> | Liste de **toutes** les familles avec rôles |

### FamilleUtilisateurDTO
| Champ | Type | Description |
|-------|------|-------------|
| `idFamille` | Long | Identifiant unique de la famille |
| `nomFamille` | String | Nom de la famille |
| `descriptionFamille` | String | Description de la famille |
| `ethnie` | String | Ethnie de la famille |
| `region` | String | Région d'origine de la famille |
| `roleFamille` | String | Rôle de l'utilisateur dans cette famille |
| `lienParente` | String | Lien de parenté (Père, Mère, Fils, etc.) |
| `dateAjout` | LocalDateTime | Date d'ajout à la famille |

### ⭐ UtilisateurAvecRoleFamilleDTO (pour endpoint 3)
| Champ | Type | Description |
|-------|------|-------------|
| `id` | Long | Identifiant unique de l'utilisateur |
| `nom` | String | Nom de famille |
| `prenom` | String | Prénom |
| `email` | String | Adresse email (unique) |
| `numeroTelephone` | String | Numéro de téléphone |
| `ethnie` | String | Ethnie de l'utilisateur |
| `role` | String | Rôle global (ROLE_ADMIN, ROLE_MEMBRE) |
| `actif` | Boolean | Statut actif/inactif |
| `dateCreation` | LocalDateTime | Date de création du compte |
| `idFamille` | Long | ID de la famille spécifique |
| `nomFamille` | String | Nom de la famille spécifique |
| `roleFamille` | String | Rôle dans **cette** famille uniquement |
| `lienParente` | String | Lien de parenté dans cette famille |
| `dateAjoutFamille` | LocalDateTime | Date d'ajout à cette famille |

## 🔐 Rôles dans les familles

### ADMIN
- Peut gérer tous les aspects de la famille
- Peut inviter de nouveaux membres
- Peut changer les rôles des autres membres
- Peut demander la publication de contenus
- Peut supprimer la famille

### EDITEUR
- Peut créer et modifier des contenus
- Peut créer des quiz
- Peut ajouter des membres à l'arbre généalogique
- Peut consulter tous les contenus de la famille
- Ne peut pas inviter de nouveaux membres
- Ne peut pas changer les rôles

### LECTEUR
- Peut consulter les contenus de la famille
- Peut passer les quiz
- Peut consulter l'arbre généalogique
- Ne peut pas créer de contenus
- Ne peut pas inviter de nouveaux membres
- **Rôle par défaut** lors de l'acceptation d'une invitation

## 🔒 Sécurité

✅ Le **mot de passe n'est JAMAIS inclus** dans les réponses
✅ Seules les informations publiques de l'utilisateur sont retournées
✅ Les familles privées ne sont visibles que par leurs membres

## 🚨 Codes de réponse

| Code | Description |
|------|-------------|
| 200 | Utilisateur trouvé avec succès |
| 404 | Utilisateur non trouvé |

## 📝 Exemples d'utilisation

### Récupération avec curl
```bash
# Par ID (toutes les familles)
curl -X GET "http://localhost:8080/api/utilisateurs/1" \
  -H "accept: application/json"

# Par email (toutes les familles)
curl -X GET "http://localhost:8080/api/utilisateurs/email/mamadou.diallo@example.com" \
  -H "accept: application/json"

# Par ID utilisateur et ID famille (rôle dans UNE famille)
curl -X GET "http://localhost:8080/api/utilisateurs/1/famille/3" \
  -H "accept: application/json"
```

### Avec JavaScript (Fetch API)
```javascript
// Par ID (toutes les familles)
fetch('http://localhost:8080/api/utilisateurs/1')
  .then(response => response.json())
  .then(utilisateur => {
    console.log('Utilisateur:', utilisateur);
    console.log('Nombre de familles:', utilisateur.familles.length);
    utilisateur.familles.forEach(famille => {
      console.log(`- ${famille.nomFamille} (${famille.roleFamille})`);
    });
  });

// Par email (toutes les familles)
fetch('http://localhost:8080/api/utilisateurs/email/mamadou.diallo@example.com')
  .then(response => response.json())
  .then(utilisateur => {
    console.log('Utilisateur:', utilisateur);
  });

// Par ID utilisateur et ID famille (rôle dans UNE famille)
fetch('http://localhost:8080/api/utilisateurs/1/famille/3')
  .then(response => response.json())
  .then(utilisateur => {
    console.log('Utilisateur:', utilisateur.nom, utilisateur.prenom);
    console.log('Famille:', utilisateur.nomFamille);
    console.log('Rôle:', utilisateur.roleFamille);
    console.log('Lien de parenté:', utilisateur.lienParente);
  })
  .catch(error => {
    console.error('Erreur: Utilisateur non membre de cette famille');
  });
```

## 🔗 Fichiers concernés

### Controllers
- `src/main/java/com/heritage/controller/UtilisateurController.java` - Nouveau controller avec 3 endpoints

### Services
- `src/main/java/com/heritage/service/UtilisateurService.java` - Service avec méthodes :
  - `getUserById()` - Récupère toutes les familles
  - `getUserByEmail()` - Récupère toutes les familles
  - `getUserWithRoleInFamille()` - ⭐ Récupère le rôle dans UNE famille

### DTOs
- `src/main/java/com/heritage/dto/UtilisateurDTO.java` - DTO avec liste de toutes les familles
- `src/main/java/com/heritage/dto/FamilleUtilisateurDTO.java` - DTO pour une famille dans la liste
- `src/main/java/com/heritage/dto/UtilisateurAvecRoleFamilleDTO.java` - ⭐ Nouveau DTO pour rôle dans UNE famille

### Repositories
- `src/main/java/com/heritage/repository/MembreFamilleRepository.java` - Méthode `findByUtilisateurIdAndFamilleId()`

### Entités
- `src/main/java/com/heritage/entite/Utilisateur.java` - Entité utilisateur
- `src/main/java/com/heritage/entite/MembreFamille.java` - Relation utilisateur-famille
- `src/main/java/com/heritage/entite/Famille.java` - Entité famille
- `src/main/java/com/heritage/entite/RoleFamille.java` - Enum des rôles

## 📌 Notes importantes

1. **Performance** : Les familles sont chargées avec l'utilisateur en utilisant `@OneToMany` avec `FetchType.LAZY`
2. **Lien de parenté** : Le champ `lienParente` peut être null si non défini
3. **Rôle par défaut** : Lors de l'ajout à une famille, le rôle par défaut est `LECTEUR`
4. **Unicité** : Un utilisateur ne peut avoir qu'un seul rôle par famille (contrainte unique)
5. **Choix de l'endpoint** :
   - Utilisez les endpoints 1 ou 2 si vous avez besoin de **toutes les familles** de l'utilisateur
   - Utilisez l'endpoint 3 si vous avez besoin du rôle dans **une seule famille spécifique**

## 🎓 Documentation Swagger

L'API est documentée avec Swagger/OpenAPI. Accédez à :
```
http://localhost:8080/swagger-ui/index.html
```

Cherchez le tag **"👤 Utilisateurs"** pour voir tous les endpoints disponibles.

