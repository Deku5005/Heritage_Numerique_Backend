# 📚 **DOCUMENTATION SWAGGER - HERITAGE NUMÉRIQUE**

## 🎯 **Vue d'Ensemble**

Swagger/OpenAPI 3 a été intégré à l'application Heritage Numérique pour générer automatiquement une documentation interactive de l'API REST.

---

## 🚀 **Accès à la Documentation**

### **URLs de Documentation**
- **Interface Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **Documentation JSON** : `http://localhost:8080/api-docs`
- **Documentation YAML** : `http://localhost:8080/api-docs.yaml`

### **Interface Interactive**
L'interface Swagger UI permet de :
- ✅ **Tester les endpoints** directement depuis le navigateur
- ✅ **Voir les schémas** de requête et réponse
- ✅ **Authentification intégrée** avec JWT
- ✅ **Exemples de données** pour chaque endpoint
- ✅ **Validation en temps réel** des requêtes

---

## 🔐 **Authentification dans Swagger**

### **Étapes pour s'authentifier :**

1. **Ouvrir Swagger UI** : `http://localhost:8080/swagger-ui.html`

2. **S'inscrire ou se connecter** :
   - Utiliser l'endpoint `POST /api/auth/register` pour s'inscrire
   - Utiliser l'endpoint `POST /api/auth/login` pour se connecter

3. **Copier le token JWT** de la réponse

4. **Cliquer sur "Authorize"** (bouton vert en haut à droite)

5. **Entrer le token** : `Bearer {votre_token_jwt}`

6. **Cliquer sur "Authorize"**

7. **Tester les endpoints protégés** !

---

## 📋 **Fonctionnalités de Swagger**

### **1. Documentation Automatique**
- ✅ **37 endpoints documentés** automatiquement
- ✅ **Schémas de données** générés automatiquement
- ✅ **Exemples de requêtes/réponses** fournis
- ✅ **Codes d'erreur** documentés

### **2. Interface Interactive**
- ✅ **Test en temps réel** des endpoints
- ✅ **Validation des données** avant envoi
- ✅ **Affichage des réponses** formatées
- ✅ **Gestion des erreurs** avec détails

### **3. Authentification Intégrée**
- ✅ **Support JWT** natif
- ✅ **Token persistant** pendant la session
- ✅ **Test des endpoints protégés** facilité

### **4. Organisation par Tags**
- 🔐 **Authentification** : Inscription, connexion
- 👨‍👩‍👧‍👦 **Gestion des Familles** : Création, invitation
- 📧 **Gestion des Invitations** : Accepter, refuser
- 📚 **Gestion des Contenus** : Contes, artisanats, proverbes, devinettes
- 🌐 **Traduction Automatique** : Services de traduction
- 📊 **Dashboard et Statistiques** : Données personnelles et familiales
- 🔧 **Super-Admin** : Gestion globale
- 🎯 **Quiz et Arbre Généalogique** : Quiz interactifs et arbre familial

---

## 🛠️ **Configuration Swagger**

### **Dépendances Maven**
```xml
<!-- SpringDoc OpenAPI 3 (Swagger UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### **Configuration dans application.properties**
```properties
# Configuration Swagger/OpenAPI 3
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.display-operation-id=true
```

### **Configuration Java**
```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
}
```

---

## 📝 **Annotations Swagger Utilisées**

### **1. Annotations de Classe**
```java
@Tag(name = "🔐 Authentification", description = "Endpoints pour l'authentification")
@SecurityRequirement(name = "Bearer Authentication")
```

### **2. Annotations de Méthode**
```java
@Operation(
    summary = "Inscription d'un nouvel utilisateur",
    description = "Description détaillée du processus d'inscription",
    tags = {"🔐 Authentification"}
)
```

### **3. Annotations de Réponse**
```java
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "201",
        description = "Utilisateur créé avec succès",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = AuthResponse.class),
            examples = @ExampleObject(
                name = "Inscription réussie",
                value = "{\"accessToken\": \"...\", \"tokenType\": \"Bearer\"}"
            )
        )
    )
})
```

### **4. Annotations de Paramètre**
```java
@Parameter(description = "Données d'inscription de l'utilisateur", required = true)
@Valid @RequestBody RegisterRequest request
```

---

## 🎯 **Exemples d'Utilisation**

### **1. Test d'Inscription**
1. Aller sur `POST /api/auth/register`
2. Cliquer sur "Try it out"
3. Modifier le JSON d'exemple :
```json
{
  "nom": "Traoré",
  "prenom": "Amadou",
  "email": "amadou@example.com",
  "numeroTelephone": "+223 70 12 34 56",
  "ethnie": "Bambara",
  "motDePasse": "password123"
}
```
4. Cliquer sur "Execute"
5. Copier le token de la réponse

### **2. Test d'Authentification**
1. Cliquer sur "Authorize"
2. Entrer : `Bearer {votre_token}`
3. Cliquer sur "Authorize"
4. Tester les endpoints protégés

### **3. Test de Création de Famille**
1. Aller sur `POST /api/familles`
2. Cliquer sur "Try it out"
3. Modifier le JSON :
```json
{
  "nom": "Famille Traoré",
  "description": "Famille élargie des Traoré de Bamako",
  "ethnie": "Bambara",
  "region": "District de Bamako"
}
```
4. Cliquer sur "Execute"

---

## 🔧 **Personnalisation Avancée**

### **1. Ajout d'Annotations à un Nouveau Contrôleur**
```java
@RestController
@RequestMapping("/api/nouveau")
@Tag(name = "🆕 Nouveau Module", description = "Description du module")
@SecurityRequirement(name = "Bearer Authentication")
public class NouveauController {
    
    @Operation(
        summary = "Nouvelle fonctionnalité",
        description = "Description détaillée",
        tags = {"🆕 Nouveau Module"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Succès"),
        @ApiResponse(responseCode = "400", description = "Erreur de validation")
    })
    @PostMapping("/endpoint")
    public ResponseEntity<?> nouvelleMethode(@RequestBody RequestDTO request) {
        // Implémentation
    }
}
```

### **2. Documentation des DTOs**
```java
@Schema(description = "Requête d'inscription d'un utilisateur")
public class RegisterRequest {
    
    @Schema(description = "Nom de famille de l'utilisateur", example = "Traoré")
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @Schema(description = "Prénom de l'utilisateur", example = "Amadou")
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;
    
    @Schema(description = "Email de l'utilisateur", example = "amadou@example.com")
    @Email(message = "L'email doit être valide")
    private String email;
}
```

---

## 🚀 **Avantages de Swagger**

### **1. Pour les Développeurs**
- ✅ **Documentation automatique** : Plus besoin de maintenir la documentation manuellement
- ✅ **Test intégré** : Tester l'API directement depuis l'interface
- ✅ **Validation visuelle** : Voir les erreurs avant l'envoi
- ✅ **Exemples fournis** : Comprendre rapidement l'utilisation

### **2. Pour les Testeurs**
- ✅ **Interface intuitive** : Tester sans connaissance technique approfondie
- ✅ **Scénarios complets** : Tester tous les cas d'usage
- ✅ **Authentification simplifiée** : Gestion automatique des tokens
- ✅ **Résultats visuels** : Voir les réponses formatées

### **3. Pour les Intégrateurs**
- ✅ **Documentation complète** : Tous les endpoints documentés
- ✅ **Schémas de données** : Comprendre la structure des données
- ✅ **Codes d'erreur** : Gestion d'erreurs documentée
- ✅ **Authentification** : Processus d'auth expliqué

---

## 🎉 **Conclusion**

Swagger/OpenAPI 3 transforme l'API Heritage Numérique en une interface interactive et documentée qui facilite :

- ✅ **Le développement** : Documentation automatique et tests intégrés
- ✅ **L'intégration** : Interface claire pour les développeurs externes
- ✅ **Les tests** : Validation complète des fonctionnalités
- ✅ **La maintenance** : Documentation toujours à jour

**🚀 L'API Heritage Numérique est maintenant entièrement documentée et testable via Swagger UI !**

---

## 📞 **Support**

Pour toute question sur l'utilisation de Swagger :
- 📧 Email : support@heritage-numerique.com
- 🌐 Documentation : https://heritage-numerique.com/docs
- 📚 Swagger UI : http://localhost:8080/swagger-ui.html
