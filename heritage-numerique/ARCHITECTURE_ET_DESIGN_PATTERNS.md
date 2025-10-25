# 🏗️ **ARCHITECTURE ET DESIGN PATTERNS - HERITAGE NUMÉRIQUE**

## 📋 **Vue d'Ensemble de l'Architecture**

L'application Heritage Numérique utilise l'architecture **MVC (Model-View-Controller)** avec Spring Boot, suivant les principes de l'architecture en couches et les bonnes pratiques de développement.

---

## 🎯 **Architecture MVC**

### **1. MODEL (Modèle)**
- **Entités JPA** : `@Entity` classes représentant les tables de base de données
- **Repositories** : Interfaces `JpaRepository` pour l'accès aux données
- **DTOs** : Data Transfer Objects pour la communication entre couches

### **2. VIEW (Vue)**
- **REST Controllers** : `@RestController` exposant les endpoints API
- **JSON Responses** : Format de réponse standardisé
- **Documentation** : `POSTMAN_ENDPOINTS.md` pour la documentation API

### **3. CONTROLLER (Contrôleur)**
- **Controllers** : `@Controller` gérant les requêtes HTTP
- **Services** : `@Service` contenant la logique métier
- **Security** : `@PreAuthorize` pour la sécurité

---

## 🏛️ **Structure des Couches**

```
┌─────────────────────────────────────┐
│           PRESENTATION             │
│  Controllers (@RestController)     │
├─────────────────────────────────────┤
│           BUSINESS LOGIC           │
│  Services (@Service)               │
├─────────────────────────────────────┤
│           DATA ACCESS              │
│  Repositories (JpaRepository)      │
├─────────────────────────────────────┤
│           DATABASE                 │
│  MySQL Database                    │
└─────────────────────────────────────┘
```

---

## 🎨 **Design Patterns Implémentés**

### **1. Repository Pattern**
```java
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRole(String role);
}
```

**Avantages :**
- ✅ Abstraction de l'accès aux données
- ✅ Testabilité améliorée
- ✅ Séparation des responsabilités

### **2. Service Layer Pattern**
```java
@Service
public class AuthService {
    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;
    
    public AuthResponse login(LoginRequest request) {
        // Logique métier
    }
}
```

**Avantages :**
- ✅ Logique métier centralisée
- ✅ Réutilisabilité
- ✅ Transactional management

### **3. DTO Pattern**
```java
@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String email;
    private String role;
}
```

**Avantages :**
- ✅ Sérialisation contrôlée
- ✅ Sécurité des données
- ✅ Versioning des APIs

### **4. Builder Pattern**
```java
@Builder
public class Utilisateur {
    private String nom;
    private String prenom;
    private String email;
    // ...
}
```

**Avantages :**
- ✅ Construction d'objets complexes
- ✅ Code lisible
- ✅ Paramètres optionnels

### **5. Strategy Pattern (Traduction)**
```java
@Service
public class ServiceTraductionHuggingFace {
    public String traduire(String texte, String langueCible) {
        // Stratégie de traduction HuggingFace
    }
}
```

**Avantages :**
- ✅ Algorithmes interchangeables
- ✅ Extensibilité
- ✅ Testabilité

### **6. Factory Pattern (JWT)**
```java
@Service
public class JwtService {
    public String generateToken(UserDetails userDetails, Long userId, String role) {
        // Factory pour créer des tokens JWT
    }
}
```

**Avantages :**
- ✅ Création d'objets complexes
- ✅ Encapsulation
- ✅ Configuration centralisée

---

## 🔧 **Patterns de Sécurité**

### **1. Authentication Pattern**
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/dashboard")
public ResponseEntity<DashboardDTO> getDashboard() {
    // Accès restreint aux administrateurs
}
```

### **2. Authorization Pattern**
```java
@PreAuthorize("hasRole('MEMBRE')")
public class ContenuController {
    // Accès aux membres authentifiés
}
```

### **3. JWT Token Pattern**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Filtre JWT pour l'authentification
}
```

---

## 📊 **Patterns de Gestion des Données**

### **1. Transactional Pattern**
```java
@Transactional
public void createFamille(FamilleRequest request) {
    // Opérations atomiques
}
```

### **2. Lazy Loading Pattern**
```java
@OneToMany(fetch = FetchType.LAZY)
private List<Contenu> contenus;
```

### **3. Cascade Pattern**
```java
@OneToMany(cascade = CascadeType.ALL, mappedBy = "famille")
private List<MembreFamille> membres;
```

---

## 🎯 **Patterns de Communication**

### **1. RESTful API Pattern**
```java
@RestController
@RequestMapping("/api/familles")
public class FamilleController {
    @GetMapping
    public ResponseEntity<List<FamilleDTO>> getAllFamilles() {
        // GET /api/familles
    }
    
    @PostMapping
    public ResponseEntity<FamilleDTO> createFamille(@RequestBody FamilleRequest request) {
        // POST /api/familles
    }
}
```

### **2. Response Entity Pattern**
```java
public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authResponse);
}
```

### **3. Exception Handling Pattern**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        // Gestion centralisée des erreurs
    }
}
```

---

## 🔄 **Patterns de Traduction**

### **1. Service Locator Pattern**
```java
@Service
public class ServiceTraductionHuggingFace {
    // Service de traduction centralisé
}
```

### **2. Template Method Pattern**
```java
public abstract class TraductionService {
    public final String traduire(String texte, String langue) {
        // Template method
        return processTraduction(texte, langue);
    }
    
    protected abstract String processTraduction(String texte, String langue);
}
```

---

## 📈 **Patterns de Performance**

### **1. Caching Pattern**
```java
@Cacheable("familles")
public List<FamilleDTO> getAllFamilles() {
    // Cache des familles
}
```

### **2. Lazy Loading Pattern**
```java
@OneToMany(fetch = FetchType.LAZY)
private List<Contenu> contenus;
```

### **3. Pagination Pattern**
```java
@GetMapping
public ResponseEntity<Page<ContenuDTO>> getContenus(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    // Pagination des résultats
}
```

---

## 🎨 **Patterns de Configuration**

### **1. Configuration Properties Pattern**
```java
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {
    private String secret;
    private Long expiration;
    private String issuer;
}
```

### **2. Bean Configuration Pattern**
```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 🔍 **Patterns de Validation**

### **1. Validation Pattern**
```java
@Data
public class RegisterRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @Email(message = "Email invalide")
    private String email;
}
```

### **2. Custom Validation Pattern**
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleValidator.class)
public @interface ValidRole {
    String message() default "Rôle invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

---

## 📋 **Résumé des Patterns Utilisés**

| Pattern | Implémentation | Avantages |
|---------|----------------|-----------|
| **Repository** | `JpaRepository` | Abstraction données |
| **Service Layer** | `@Service` | Logique métier |
| **DTO** | `@Data @Builder` | Transfert sécurisé |
| **Builder** | `@Builder` | Construction objets |
| **Strategy** | Services traduction | Algorithmes flexibles |
| **Factory** | `JwtService` | Création tokens |
| **Authentication** | `@PreAuthorize` | Sécurité |
| **Transactional** | `@Transactional` | Atomicité |
| **RESTful** | `@RestController` | API standardisée |
| **Exception Handler** | `@ControllerAdvice` | Gestion erreurs |

---

## 🎯 **Avantages de l'Architecture**

### **1. Séparation des Responsabilités**
- ✅ **Controllers** : Gestion des requêtes HTTP
- ✅ **Services** : Logique métier
- ✅ **Repositories** : Accès aux données
- ✅ **DTOs** : Transfert de données

### **2. Testabilité**
- ✅ **Unit Tests** : Services et repositories
- ✅ **Integration Tests** : Controllers
- ✅ **Mocking** : Dependencies injection

### **3. Maintenabilité**
- ✅ **Code modulaire** : Chaque classe a une responsabilité
- ✅ **Documentation** : Javadoc et annotations
- ✅ **Configuration** : Externalisée dans properties

### **4. Extensibilité**
- ✅ **Nouveaux endpoints** : Ajout facile
- ✅ **Nouvelles fonctionnalités** : Services modulaires
- ✅ **Nouveaux patterns** : Architecture flexible

### **5. Sécurité**
- ✅ **JWT Authentication** : Tokens sécurisés
- ✅ **Role-based Access** : Contrôle d'accès
- ✅ **Input Validation** : Validation des données

---

## 🚀 **Conclusion**

L'architecture Heritage Numérique respecte les principes SOLID et utilise les design patterns appropriés pour créer une application :

- ✅ **Maintenable** : Code organisé et documenté
- ✅ **Testable** : Séparation des responsabilités
- ✅ **Extensible** : Patterns modulaires
- ✅ **Sécurisée** : Authentification et autorisation
- ✅ **Performante** : Optimisations et caching

Cette architecture permet une évolution continue de l'application tout en maintenant la qualité du code et la sécurité des données.
