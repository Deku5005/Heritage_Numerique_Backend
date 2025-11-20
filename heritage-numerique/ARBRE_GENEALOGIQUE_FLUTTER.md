# Guide d'Affichage de l'Arbre Généalogique dans Flutter

## 📋 Structure de la Réponse API

### Endpoint
```
GET /api/arbre-genealogique/famille/{familleId}/hierarchique
Authorization: Bearer {token}
```

### Structure de la Réponse JSON

La réponse est structurée de manière hiérarchique, optimisée pour l'affichage dans Flutter :

```json
{
  "id": 1,
  "idFamille": 1,
  "nomFamille": "Famille Traoré",
  "nom": "Arbre généalogique de Famille Traoré",
  "description": "Arbre généalogique de la famille Traoré",
  "idCreateur": 1,
  "nomCreateur": "Traoré Amadou",
  "dateCreation": "2024-01-10T10:00:00",
  "dateModification": "2024-01-10T10:00:00",
  "racines": [
    {
      "id": 1,
      "nom": "Traoré",
      "prenom": "Amadou",
      "nomComplet": "Traoré Amadou",
      "sexe": "M",
      "dateNaissance": "1950-05-15",
      "dateDeces": null,
      "lieuNaissance": "Bamako",
      "lieuDeces": null,
      "biographie": "Chef de famille traditionnel",
      "photoUrl": "images/uuid.jpg",
      "relationFamiliale": "Chef de famille",
      "idPere": null,
      "idMere": null,
      "enfants": [
        {
          "id": 2,
          "nom": "Traoré",
          "prenom": "Fatoumata",
          "nomComplet": "Traoré Fatoumata",
          "sexe": "F",
          "dateNaissance": "1975-03-20",
          "dateDeces": null,
          "lieuNaissance": "Bamako",
          "lieuDeces": null,
          "biographie": null,
          "photoUrl": "images/uuid2.jpg",
          "relationFamiliale": "Fille",
          "idPere": 1,
          "idMere": 3,
          "enfants": [],
          "nombreEnfants": 0,
          "niveau": 1,
          "positionX": 0.0,
          "positionY": 1.0
        },
        {
          "id": 4,
          "nom": "Traoré",
          "prenom": "Ibrahim",
          "nomComplet": "Traoré Ibrahim",
          "sexe": "M",
          "dateNaissance": "1980-07-10",
          "dateDeces": null,
          "lieuNaissance": "Bamako",
          "lieuDeces": null,
          "biographie": null,
          "photoUrl": "images/uuid3.jpg",
          "relationFamiliale": "Fils",
          "idPere": 1,
          "idMere": 3,
          "enfants": [
            {
              "id": 5,
              "nom": "Traoré",
              "prenom": "Aissata",
              "nomComplet": "Traoré Aissata",
              "sexe": "F",
              "dateNaissance": "2005-12-25",
              "dateDeces": null,
              "lieuNaissance": "Bamako",
              "lieuDeces": null,
              "biographie": null,
              "photoUrl": null,
              "relationFamiliale": "Petite-fille",
              "idPere": 4,
              "idMere": 6,
              "enfants": [],
              "nombreEnfants": 0,
              "niveau": 2,
              "positionX": 0.5,
              "positionY": 2.0
            }
          ],
          "nombreEnfants": 1,
          "niveau": 1,
          "positionX": 1.0,
          "positionY": 1.0
        }
      ],
      "nombreEnfants": 2,
      "niveau": 0,
      "positionX": 0.0,
      "positionY": 0.0
    }
  ],
  "nombreMembres": 5,
  "nombreGenerations": 3,
  "idMembreRacinePrincipal": 1
}
```

### Explication des Champs

- **racines** : Liste des membres sans parents (ancêtres les plus anciens)
- **enfants** : Liste récursive des enfants de chaque membre
- **niveau** : Profondeur dans l'arbre (0 = racine, 1 = enfants, etc.)
- **positionX / positionY** : Coordonnées suggérées pour le positionnement visuel
- **nombreEnfants** : Nombre d'enfants directs
- **nombreGenerations** : Nombre total de générations dans l'arbre

---

## 🎨 Packages Flutter Recommandés

### Option 1 : **graphview** (Recommandé pour MyHeritage-like)

**Package** : `graphview`

**Installation** :
```yaml
dependencies:
  graphview: ^1.2.0
```

**Avantages** :
- ✅ Supporte les graphes hiérarchiques complexes
- ✅ Layout automatique (Buchheim, Fruchterman-Reingold, etc.)
- ✅ Zoom et pan intégrés
- ✅ Personnalisation des nœuds et des connexions
- ✅ Idéal pour les arbres généalogiques

**Exemple d'utilisation** :
```dart
import 'package:graphview/GraphView.dart';

class ArbreGenealogiqueWidget extends StatelessWidget {
  final ArbreGenealogiqueHierarchiqueDTO arbre;
  
  @override
  Widget build(BuildContext context) {
    Graph graph = Graph();
    
    // Construire le graphe à partir de la structure hiérarchique
    Map<Long, Node> nodeMap = {};
    
    void buildGraph(NoeudArbreDTO noeud, Node? parentNode) {
      Node node = Node.Id(noeud.id);
      nodeMap[noeud.id] = node;
      graph.addNode(node);
      
      if (parentNode != null) {
        graph.addEdge(parentNode, node);
      }
      
      for (var enfant in noeud.enfants) {
        buildGraph(enfant, node);
      }
    }
    
    // Construire le graphe depuis les racines
    for (var racine in arbre.racines) {
      buildGraph(racine, null);
    }
    
    return InteractiveViewer(
      constrained: false,
      child: GraphView(
        graph: graph,
        algorithm: BuchheimWalkerAlgorithm(
          builder: (Node node) {
            final noeud = getNoeudById(node.key!.value);
            return _buildMembreCard(noeud);
          },
        ),
      ),
    );
  }
  
  Widget _buildMembreCard(NoeudArbreDTO noeud) {
    return Container(
      width: 120,
      height: 150,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.3),
            spreadRadius: 2,
            blurRadius: 5,
          ),
        ],
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // Photo circulaire
          CircleAvatar(
            radius: 30,
            backgroundImage: noeud.photoUrl != null
                ? NetworkImage('${baseUrl}/uploads/${noeud.photoUrl}')
                : null,
            child: noeud.photoUrl == null
                ? Icon(Icons.person, size: 30)
                : null,
          ),
          SizedBox(height: 8),
          // Nom
          Text(
            noeud.nomComplet,
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.bold,
            ),
            textAlign: TextAlign.center,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
          SizedBox(height: 4),
          // Date de naissance
          if (noeud.dateNaissance != null)
            Text(
              '${noeud.dateNaissance.year}',
              style: TextStyle(fontSize: 10, color: Colors.grey),
            ),
        ],
      ),
    );
  }
}
```

---

### Option 2 : **flutter_treeview** (Plus simple, pour arbres verticaux)

**Package** : `flutter_treeview`

**Installation** :
```yaml
dependencies:
  flutter_treeview: ^1.0.0
```

**Avantages** :
- ✅ Simple à utiliser
- ✅ Vue arborescente classique
- ✅ Expansion/réduction des nœuds
- ⚠️ Moins flexible pour les arbres généalogiques complexes

---

### Option 3 : **Custom Canvas** (Maximum de contrôle)

Si vous voulez un contrôle total sur l'affichage (comme MyHeritage), utilisez `CustomPaint` avec `Canvas` :

**Avantages** :
- ✅ Contrôle total sur le design
- ✅ Performance optimale
- ✅ Animations personnalisées
- ⚠️ Plus de code à écrire

**Exemple** :
```dart
class ArbreGenealogiqueCanvas extends CustomPainter {
  final ArbreGenealogiqueHierarchiqueDTO arbre;
  final double nodeWidth = 120;
  final double nodeHeight = 150;
  final double horizontalSpacing = 200;
  final double verticalSpacing = 200;
  
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.grey
      ..strokeWidth = 2;
    
    // Dessiner les lignes de connexion
    void drawConnections(NoeudArbreDTO noeud, Offset parentPosition) {
      final nodePosition = Offset(
        noeud.positionX * horizontalSpacing,
        noeud.positionY * verticalSpacing,
      );
      
      // Ligne de connexion parent-enfant
      canvas.drawLine(parentPosition, nodePosition, paint);
      
      // Dessiner récursivement les enfants
      for (var enfant in noeud.enfants) {
        drawConnections(enfant, nodePosition);
      }
    }
    
    // Dessiner les nœuds
    void drawNodes(NoeudArbreDTO noeud) {
      final position = Offset(
        noeud.positionX * horizontalSpacing,
        noeud.positionY * verticalSpacing,
      );
      
      // Dessiner le cercle de la photo
      canvas.drawCircle(
        position,
        30,
        Paint()..color = Colors.blue,
      );
      
      // Dessiner récursivement les enfants
      for (var enfant in noeud.enfants) {
        drawNodes(enfant);
      }
    }
    
    // Dessiner depuis les racines
    for (var racine in arbre.racines) {
      final rootPosition = Offset(
        racine.positionX * horizontalSpacing,
        racine.positionY * verticalSpacing,
      );
      
      drawNodes(racine);
      for (var enfant in racine.enfants) {
        drawConnections(enfant, rootPosition);
      }
    }
  }
  
  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
```

---

## 🎯 Recommandation Finale

Pour un affichage **similaire à MyHeritage**, je recommande :

1. **graphview** avec l'algorithme `BuchheimWalkerAlgorithm` pour un layout automatique
2. Utiliser `InteractiveViewer` pour le zoom et le pan
3. Créer des widgets personnalisés pour les cartes de membres (photo circulaire, nom, dates)
4. Ajouter des animations lors du chargement et de la navigation

### Structure Flutter Recommandée

```
lib/
  models/
    arbre_genealogique.dart          # Modèles de données
  widgets/
    arbre_genealogique_view.dart      # Widget principal
    membre_card.dart                  # Carte d'un membre
    connection_line.dart              # Ligne de connexion
  services/
    arbre_service.dart                # Service API
```

### Fonctionnalités à Implémenter

1. ✅ **Zoom et Pan** : Utiliser `InteractiveViewer`
2. ✅ **Sélection de membre** : Tap sur une carte pour voir les détails
3. ✅ **Navigation** : Centrer sur un membre spécifique
4. ✅ **Chargement progressif** : Charger les générations au fur et à mesure
5. ✅ **Animations** : Transitions fluides lors de la navigation

---

## 📱 Exemple de Modèle Flutter

```dart
// models/arbre_genealogique.dart
class ArbreGenealogiqueHierarchique {
  final int id;
  final int idFamille;
  final String nomFamille;
  final List<NoeudArbre> racines;
  final int nombreMembres;
  final int nombreGenerations;
  
  ArbreGenealogiqueHierarchique.fromJson(Map<String, dynamic> json)
      : id = json['id'],
        idFamille = json['idFamille'],
        nomFamille = json['nomFamille'],
        racines = (json['racines'] as List)
            .map((r) => NoeudArbre.fromJson(r))
            .toList(),
        nombreMembres = json['nombreMembres'],
        nombreGenerations = json['nombreGenerations'];
}

class NoeudArbre {
  final int id;
  final String nomComplet;
  final String? photoUrl;
  final DateTime? dateNaissance;
  final List<NoeudArbre> enfants;
  final int niveau;
  final double positionX;
  final double positionY;
  
  NoeudArbre.fromJson(Map<String, dynamic> json)
      : id = json['id'],
        nomComplet = json['nomComplet'],
        photoUrl = json['photoUrl'],
        dateNaissance = json['dateNaissance'] != null
            ? DateTime.parse(json['dateNaissance'])
            : null,
        enfants = (json['enfants'] as List)
            .map((e) => NoeudArbre.fromJson(e))
            .toList(),
        niveau = json['niveau'],
        positionX = json['positionX']?.toDouble() ?? 0.0,
        positionY = json['positionY']?.toDouble() ?? 0.0;
}
```

---

## 🔗 Ressources

- [graphview Package](https://pub.dev/packages/graphview)
- [flutter_treeview Package](https://pub.dev/packages/flutter_treeview)
- [InteractiveViewer Documentation](https://api.flutter.dev/flutter/widgets/InteractiveViewer-class.html)
- [CustomPaint Documentation](https://api.flutter.dev/flutter/rendering/CustomPainter-class.html)

