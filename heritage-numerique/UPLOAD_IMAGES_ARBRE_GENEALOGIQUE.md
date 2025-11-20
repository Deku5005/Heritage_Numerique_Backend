# 📸 Upload d'Images pour l'Arbre Généalogique

## 🔄 Adaptation Réalisée

L'endpoint d'ajout de membre dans l'arbre généalogique utilise maintenant **la même logique d'upload que les contenus** pour assurer la cohérence dans toute l'application.

---

## 📋 Processus d'Upload

### 1. **Côté Backend (Java)**

#### Endpoint
```
POST /api/arbre-genealogique/ajouter-membre
Content-Type: multipart/form-data
```

#### Paramètres
- `photo` : Fichier image (MultipartFile, optionnel)
- `nomComplet` : String (obligatoire)
- `dateNaissance` : String (obligatoire)
- `lieuNaissance` : String (obligatoire)
- `relationFamiliale` : String (obligatoire)
- `telephone` : String (optionnel)
- `email` : String (optionnel)
- `biographie` : String (optionnel)
- `parent1Id` : Long (optionnel)
- `parent2Id` : Long (optionnel)
- `idFamille` : Long (obligatoire)

#### Code Modifié : `ArbreGenealogiqueService.java`

**Avant** :
```java
// Retirait le préfixe /uploads/ pour stocker "images/uuid.jpg"
String fullPath = fileStorageService.storeFile(request.getPhoto(), "images");
String relativePath = fullPath.replace("/uploads/", "");
membreArbre.setPhotoUrl(relativePath);
```

**Après** (adapté comme ContenuCreationService) :
```java
// Utilise handleFileUpload() qui retourne le chemin complet avec /uploads/
String urlPhoto = handleFileUpload(request.getPhoto(), "photo");
membreArbre.setPhotoUrl(urlPhoto);
```

#### Nouvelle Méthode `handleFileUpload()`

```java
private String handleFileUpload(MultipartFile fichier, String type) {
    // 1. Déterminer le sous-dossier de destination
    String sousDossier = "images"; // Toutes les images vont dans "images"
    
    // 2. Déléguer au FileStorageService
    try {
        // Retourne /uploads/images/uuid.jpg (chemin complet)
        return fileStorageService.storeFile(fichier, sousDossier);
    } catch (IOException e) {
        throw new BadRequestException("Erreur lors de la sauvegarde du fichier: " + e.getMessage());
    }
}
```

#### Stockage en Base de Données

Le chemin stocké en DB est maintenant : **`/uploads/images/uuid.jpg`**

**Exemple** :
- Fichier uploadé : `photo_famille.jpg`
- Nom généré : `a3f5b2c1-d4e6-7890-abcd-ef1234567890.jpg`
- Chemin stocké en DB : `/uploads/images/a3f5b2c1-d4e6-7890-abcd-ef1234567890.jpg`

---

### 2. **Côté Flutter**

#### Code Adapté : `FamilyTreeScreen_CORRIGE.dart`

Le code Flutter gère maintenant **les deux formats** pour la compatibilité :

```dart
// Construction de l'URL complète pour la photo
// Le chemin peut être soit "images/uuid.jpg" (ancien format) 
// soit "/uploads/images/uuid.jpg" (nouveau format)
final String fullPhotoUrl = (membre.photoUrl != null && membre.photoUrl!.isNotEmpty)
    ? membre.photoUrl!.startsWith('/uploads/')
        ? '$_baseUrl${membre.photoUrl!}' // Format nouveau: /uploads/images/uuid.jpg
        : '$_baseUrl/${membre.photoUrl!}' // Format ancien: images/uuid.jpg
    : '';

final bool hasPhoto = fullPhotoUrl.isNotEmpty;
```

#### URLs Générées

- **Nouveau format** : `http://10.0.2.2:8080/uploads/images/uuid.jpg`
- **Ancien format** (compatibilité) : `http://10.0.2.2:8080/images/uuid.jpg`

---

## 🔧 Service de Stockage : `FileStorageService`

### Fonctionnement

1. **Répertoire de base** : Configuré dans `application.properties`
   ```properties
   file.upload-dir=C:/Users/DOLO/.../heritage-numerique/src/main/java/com/heritage/uploads
   ```

2. **Sous-répertoire** : `images/` pour toutes les photos

3. **Nom de fichier** : UUID + extension originale
   - Exemple : `a3f5b2c1-d4e6-7890-abcd-ef1234567890.jpg`

4. **Chemin retourné** : `/uploads/images/uuid.jpg`

5. **Stockage physique** : 
   ```
   {uploadDir}/images/a3f5b2c1-d4e6-7890-abcd-ef1234567890.jpg
   ```

### Restrictions

- ❌ **PDF** : Bloqué (sauf pour les contes)
- ❌ **TXT** : Bloqué
- ✅ **Images** : JPG, PNG, GIF, etc. (autorisées)

---

## 📁 Structure des Fichiers

```
uploads/
├── images/              # Toutes les photos (contenus + arbre généalogique)
│   ├── uuid1.jpg
│   ├── uuid2.png
│   └── ...
├── conte/               # Fichiers PDF/TXT des contes
│   └── ...
├── video/               # Vidéos des artisanats
│   └── ...
└── ...
```

---

## 🔗 Configuration WebConfig

Les fichiers sont servis via le pattern `/uploads/**` configuré dans `WebConfig.java` :

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadDir + "/");
}
```

Cela permet d'accéder aux fichiers via :
```
http://localhost:8080/uploads/images/uuid.jpg
```

---

## ✅ Avantages de cette Adaptation

1. **Cohérence** : Même logique pour tous les uploads d'images
2. **Maintenabilité** : Un seul service de stockage (`FileStorageService`)
3. **Sécurité** : Validation centralisée des types de fichiers
4. **Organisation** : Toutes les images dans le même dossier `images/`
5. **Compatibilité** : Le code Flutter gère les anciens et nouveaux formats

---

## 🧪 Test de l'Upload

### Avec Postman

```http
POST http://localhost:8080/api/arbre-genealogique/ajouter-membre
Authorization: Bearer {token}
Content-Type: multipart/form-data

Body (form-data):
- nomComplet: "Jean Dupont"
- dateNaissance: "1990-05-15"
- lieuNaissance: "Paris"
- relationFamiliale: "Fils"
- photo: [Sélectionner un fichier image]
- idFamille: 1
```

### Réponse

```json
{
  "id": 13,
  "nomComplet": "Jean Dupont",
  "photoUrl": "/uploads/images/a3f5b2c1-d4e6-7890-abcd-ef1234567890.jpg",
  ...
}
```

### Vérification

L'image est accessible via :
```
http://localhost:8080/uploads/images/a3f5b2c1-d4e6-7890-abcd-ef1234567890.jpg
```

---

## 📝 Notes Importantes

1. **Format en DB** : Le chemin stocké inclut maintenant `/uploads/` (comme pour les contenus)
2. **Compatibilité Flutter** : Le code gère automatiquement les deux formats
3. **Migration** : Les anciens membres avec `images/uuid.jpg` continueront de fonctionner
4. **Nouveaux membres** : Utiliseront le format `/uploads/images/uuid.jpg`

---

## 📥 Récupération des Images

### Format de Retour dans les DTOs

Les URLs sont retournées **telles quelles** depuis la base de données, exactement comme pour les contenus :

#### Dans `MembreArbreDTO` :
```java
.photoUrl(membre.getPhotoUrl()) // Retourne directement depuis la DB
```

#### Dans `NoeudArbreDTO` (structure hiérarchique) :
```java
.photoUrl(membre.getPhotoUrl()) // Retourne directement depuis la DB
```

### Format de l'URL dans la Réponse JSON

**Nouveau format** (après adaptation) :
```json
{
  "id": 13,
  "nomComplet": "Jean Dupont",
  "photoUrl": "/uploads/images/a3f5b2c1-d4e6-7890-abcd-ef1234567890.jpg",
  ...
}
```

**Ancien format** (compatibilité) :
```json
{
  "id": 12,
  "nomComplet": "Marie Martin",
  "photoUrl": "images/old-uuid.jpg",
  ...
}
```

### Gestion dans Flutter

Le code Flutter gère automatiquement les deux formats :

```dart
final String fullPhotoUrl = (membre.photoUrl != null && membre.photoUrl!.isNotEmpty)
    ? membre.photoUrl!.startsWith('/uploads/')
        ? '$_baseUrl${membre.photoUrl!}' // Format nouveau: /uploads/images/uuid.jpg
        : '$_baseUrl/${membre.photoUrl!}' // Format ancien: images/uuid.jpg
    : '';
```

**URLs générées** :
- Nouveau : `http://10.0.2.2:8080/uploads/images/uuid.jpg`
- Ancien : `http://10.0.2.2:8080/images/uuid.jpg`

---

## 🔍 Vérification

Pour vérifier que l'upload et la récupération fonctionnent :

1. **Vérifier le fichier physique** :
   ```
   {uploadDir}/images/{uuid}.{extension}
   ```

2. **Vérifier en DB** :
   ```sql
   SELECT photo_url FROM membre_arbre WHERE id = {membreId};
   -- Devrait retourner : /uploads/images/uuid.jpg
   ```

3. **Tester l'URL directement** :
   ```
   http://localhost:8080/uploads/images/{uuid}.{extension}
   ```

4. **Vérifier dans la réponse API** :
   ```json
   GET /api/arbre-genealogique/famille/{familleId}/hierarchique
   
   Réponse :
   {
     "racines": [
       {
         "photoUrl": "/uploads/images/uuid.jpg",
         ...
       }
     ]
   }
   ```

5. **Vérifier dans Flutter** :
   - L'image doit s'afficher correctement dans les cartes des membres
   - L'URL complète doit être : `http://10.0.2.2:8080/uploads/images/uuid.jpg`

