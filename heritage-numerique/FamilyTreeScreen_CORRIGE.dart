import 'package:flutter/material.dart';
import 'dart:math';

// 🔑 IMPORTATION NÉCESSAIRE POUR LA VUE GRAPHIQUE
import 'package:graphview/GraphView.dart';

// Importez les modèles et le service
import '../model/FamilleModel.dart'; // 🔑 Importation correcte de Famille
import '../model/Membre.dart';
import '../service/ArbreGenealogiqueService.dart';

// 🔑 Assurez-vous d'avoir le bon chemin vers les écrans
import 'CreateTreeScreen.dart';
import 'AppDrawer.dart';
import 'MembresDetailsScreen.dart';

// --- Constantes de Couleurs Globales ---
const Color _mainAccentColor = Color(0xFFAA7311);
const Color _backgroundColor = Colors.white;
const Color _cardTextColor = Color(0xFF2E2E2E);
const Color _lightCardColor = Color(0xFFF7F2E8);

// 🔑 URL DE BASE POUR LES IMAGES
const String _baseUrl = "http://10.0.2.2:8080";

// --- FamilyTreeScreen ---
class FamilyTreeScreen extends StatefulWidget {
  final int familyId;

  const FamilyTreeScreen({super.key, required this.familyId});

  @override
  State<FamilyTreeScreen> createState() => _FamilyTreeScreenState();
}

// Définition du type de callback pour les cartes
typedef MemberTapCallback = void Function(int? memberId);

class _FamilyTreeScreenState extends State<FamilyTreeScreen> {
  // --- Ajout de l'état pour les données et le chargement ---
  Famille? _familleData;
  bool _isLoading = true;
  String? _errorMessage;
  final ArbreGenealogiqueService _apiService = ArbreGenealogiqueService();

  // 🔑 VARIABLES GRAPHVIEW
  final Graph graph = Graph();
  final BuchheimWalkerConfiguration builder = BuchheimWalkerConfiguration();
  // 🔑 NOUVEAU: Configuration pour FruchtermanReingoldAlgorithm
  final FruchtermanReingoldConfiguration frConfig = FruchtermanReingoldConfiguration(
    iterations: 1000,
    repulsionRate: 2000,
  );
  // 🔑 NOUVEAU: Utiliser FruchtermanReingoldAlgorithm pour mieux gérer les nœuds isolés
  bool _useFruchtermanReingold = false; // Peut être changé si nécessaire

  // 🔑 Initialisation de la map pour le suivi des nœuds déjà créés
  final Map<int, Node> _memberNodes = {};

  // 🔑 Contrôleur pour gérer la vue initiale du GraphView
  final TransformationController _transformationController = TransformationController();

  // 🔑 NOUVEAU: Variables pour calculer la taille dynamique du graphe
  double _graphWidth = 1000;
  double _graphHeight = 1000;
  int _maxLevel = 0;
  int _maxChildrenAtLevel = 0;

  @override
  void initState() {
    super.initState();
    // 🔑 CORRECTION: Initialiser le zoom avec une valeur par défaut plus visible
    _transformationController.value = Matrix4.identity()
      ..translate(100.0, 150.0)
      ..scale(0.5); // Zoom par défaut en attendant le calcul automatique
    _fetchFamilyTree();
  }

  // 🔑 Libérer le contrôleur lorsque l'état est détruit
  @override
  void dispose() {
    _transformationController.dispose();
    super.dispose();
  }

  // 🔑 LOGIQUE DE L'ARBRE MISE À JOUR (Utilise fetchArbreHierarchique et GraphView)
  Future<void> _fetchFamilyTree() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
      // Nettoyage de l'état du graphe
      graph.nodes.clear();
      graph.edges.clear();
      _memberNodes.clear();
    });

    try {
      // 🔑 CORRECTION: Utilisation de la nouvelle méthode
      final famille = await _apiService.fetchArbreHierarchique(familleId: widget.familyId);

      // 🔑 VÉRIFICATION DE DEBUG AJOUTÉE POUR CONFIRMER LES DONNÉES
      debugPrint('--- Données Membres Récupérées ---');
      debugPrint('Nombre de membres dans la liste : ${famille.membres.length}');
      if (famille.membres.isNotEmpty) {
        debugPrint('Nom du premier membre (racine) : ${famille.membres.first.nomComplet}');
      }
      debugPrint('------------------------------------');

      setState(() {
        _familleData = famille;
        _isLoading = false;
        // 🔑 CORRECTION: Utilisation de 'membres' au lieu de 'racines'
        _buildGraphFromHierarchicalData(famille.membres);
        // 🔑 NOUVEAU: Calculer la taille dynamique après construction du graphe
        _calculateGraphDimensions();
      });
      
      // 🔑 CORRECTION: Ajuster le zoom initial après que le widget soit construit
      // Utiliser un délai pour s'assurer que le contexte est disponible
      Future.delayed(const Duration(milliseconds: 100), () {
        if (mounted) {
          _adjustInitialView();
        }
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Erreur de chargement de l\'arbre généalogique : ${e.toString()}';
        _isLoading = false;
        debugPrint(_errorMessage);
      });
    }
  }

  // 🔑 NOUVELLE FONCTION: Calculer les dimensions nécessaires pour le graphe
  void _calculateGraphDimensions() {
    _maxLevel = 0;
    _maxChildrenAtLevel = 0;
    Map<int, int> childrenPerLevel = {};

    void calculateLevels(List<Membre> membres, int currentLevel) {
      _maxLevel = max(_maxLevel, currentLevel);
      childrenPerLevel[currentLevel] = (childrenPerLevel[currentLevel] ?? 0) + membres.length;
      _maxChildrenAtLevel = max(_maxChildrenAtLevel, childrenPerLevel[currentLevel] ?? 0);

      for (var membre in membres) {
        if (membre.enfants.isNotEmpty) {
          calculateLevels(membre.enfants, currentLevel + 1);
        }
      }
    }

    if (_familleData != null && _familleData!.membres.isNotEmpty) {
      calculateLevels(_familleData!.membres, 0);
    }

    // 🔑 CORRECTION: Valeurs par défaut si le calcul échoue
    if (_maxLevel == 0 && _maxChildrenAtLevel == 0) {
      _maxLevel = 1;
      _maxChildrenAtLevel = max(1, _familleData?.membres.length ?? 1);
    }

    // Calculer la largeur nécessaire (basé sur le nombre max d'enfants à un niveau)
    // Chaque carte fait 150px de large + 120px d'espacement (siblingSeparation)
    _graphWidth = max(1200.0, (_maxChildrenAtLevel * 270.0) + 300.0);

    // Calculer la hauteur nécessaire (basé sur le nombre de niveaux)
    // Chaque niveau fait 180px de hauteur (levelSeparation) + 150px pour la carte
    _graphHeight = max(1000.0, ((_maxLevel + 1) * 330.0) + 300.0);

    debugPrint('--- Dimensions du graphe calculées ---');
    debugPrint('Niveaux max: $_maxLevel');
    debugPrint('Enfants max par niveau: $_maxChildrenAtLevel');
    debugPrint('Largeur calculée: $_graphWidth');
    debugPrint('Hauteur calculée: $_graphHeight');
    debugPrint('--------------------------------------');
  }

  // 🔑 NOUVELLE FONCTION: Ajuster la vue initiale pour voir tous les nœuds
  void _adjustInitialView() {
    // 🔑 CORRECTION: Utiliser addPostFrameCallback pour s'assurer que le contexte est disponible
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      
      // Calculer le centre du graphe
      double centerX = _graphWidth / 2;
      double centerY = _graphHeight / 2;

      // Calculer le zoom nécessaire pour voir tout le graphe
      // Utiliser la taille de l'écran comme référence
      final screenWidth = MediaQuery.of(context).size.width;
      final screenHeight = MediaQuery.of(context).size.height - 300; // Réserver de l'espace pour le header et stats

      // 🔑 CORRECTION: Vérifier que les dimensions sont valides
      if (screenWidth <= 0 || screenHeight <= 0 || _graphWidth <= 0 || _graphHeight <= 0) {
        debugPrint('⚠️ Dimensions invalides, utilisation de valeurs par défaut');
        debugPrint('   screenWidth: $screenWidth, screenHeight: $screenHeight');
        debugPrint('   _graphWidth: $_graphWidth, _graphHeight: $_graphHeight');
        _transformationController.value = Matrix4.identity()
          ..translate(100.0, 150.0)
          ..scale(0.5);
        return;
      }

      // 🔑 CORRECTION CRITIQUE: Calculer le zoom pour que le graphe entier soit visible
      double scaleX = screenWidth / _graphWidth;
      double scaleY = screenHeight / _graphHeight;
      double scale = min(scaleX, scaleY) * 0.6; // 🔑 RÉDUIT à 0.6 pour voir plus de contenu

      // 🔑 CORRECTION: S'assurer que le zoom n'est pas trop petit (minimum 0.15 pour voir tous les nœuds)
      scale = scale.clamp(0.15, 1.0); // 🔑 RÉDUIT le minimum à 0.15 pour voir tous les nœuds

      // 🔑 CORRECTION CRITIQUE: Calculer la translation pour centrer le graphe correctement
      // La translation doit être calculée pour que le centre du graphe soit au centre de l'écran
      // Après le scale, le graphe a une taille de (_graphWidth * scale) x (_graphHeight * scale)
      double scaledWidth = _graphWidth * scale;
      double scaledHeight = _graphHeight * scale;
      
      // Centrer le graphe dans l'écran
      double translateX = (screenWidth - scaledWidth) / 2;
      double translateY = (screenHeight - scaledHeight) / 2 + 50; // Ajustement pour le header

      // 🔑 S'assurer que la translation n'est pas négative (cela déplacerait le graphe hors de l'écran)
      translateX = max(0.0, translateX);
      translateY = max(0.0, translateY);

      _transformationController.value = Matrix4.identity()
        ..translate(translateX, translateY)
        ..scale(scale);

      debugPrint('--- Vue initiale ajustée ---');
      debugPrint('Screen: ${screenWidth}x${screenHeight}');
      debugPrint('Graph: ${_graphWidth}x${_graphHeight}');
      debugPrint('Scaled Graph: ${scaledWidth}x${scaledHeight}');
      debugPrint('Zoom: $scale');
      debugPrint('Translation: ($translateX, $translateY)');
      debugPrint('Center Graph: ($centerX, $centerY)');
      debugPrint('----------------------------');
    });
  }

  // 🔑 FONCTION DE CONSTRUCTION DU GRAPHE RÉCURSIVE
  void _buildGraphFromHierarchicalData(List<Membre> membres) {
    if (membres.isEmpty) return;

    // 🔑 CORRECTION: Configuration améliorée du layout avec plus d'espace
    builder
      ..orientation = (BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
      ..siblingSeparation = (120) // 🔑 AUGMENTÉ de 100 à 120 pour plus d'espace horizontal
      ..levelSeparation = (180) // 🔑 AUGMENTÉ de 150 à 180 pour plus d'espace vertical
      ..subtreeSeparation = (120); // 🔑 AUGMENTÉ de 100 à 120

    // Ajout de chaque racine au graphe
    for (var racine in membres) {
      _addMemberAndChildrenToGraph(null, racine);
    }

    // 🔑 DEBUG : Afficher le nombre total de nœuds ajoutés
    debugPrint('Total nodes added to graph: ${graph.nodes.length}');
    debugPrint('Total edges added to graph: ${graph.edges.length}');
    
    // 🔑 NOUVEAU: Vérifier quels nœuds sont connectés
    Set<int> connectedNodes = {};
    for (var edge in graph.edges) {
      int? sourceId = edge.source.key?.value as int?;
      int? destId = edge.destination.key?.value as int?;
      if (sourceId != null) connectedNodes.add(sourceId);
      if (destId != null) connectedNodes.add(destId);
    }
    
    // Trouver les nœuds isolés (sans arêtes)
    Set<int> isolatedNodes = {};
    for (var node in graph.nodes) {
      int? nodeId = node.key?.value as int?;
      if (nodeId != null && !connectedNodes.contains(nodeId)) {
        isolatedNodes.add(nodeId);
      }
    }
    
    debugPrint('--- Analyse de la connectivité ---');
    debugPrint('Nœuds connectés: ${connectedNodes.length}');
    debugPrint('Nœuds isolés: ${isolatedNodes.length}');
    if (isolatedNodes.isNotEmpty) {
      debugPrint('⚠️ Nœuds isolés (non visibles): $isolatedNodes');
      debugPrint('💡 Solution: Ces nœuds n\'ont pas d\'arêtes et ne seront pas affichés par BuchheimWalkerAlgorithm');
    }
    debugPrint('----------------------------------');
    
    // 🔑 CORRECTION: Si des nœuds sont isolés, créer un nœud racine virtuel pour les connecter
    if (isolatedNodes.isNotEmpty) {
      debugPrint('🔧 Création d\'un nœud racine virtuel pour connecter les nœuds isolés...');
      // Créer un nœud virtuel (ID négatif pour éviter les conflits)
      Node virtualRoot = Node.Id(-1);
      graph.addNode(virtualRoot);
      
      // Connecter tous les nœuds isolés au nœud virtuel
      for (int isolatedId in isolatedNodes) {
        Node? isolatedNode = _memberNodes[isolatedId];
        if (isolatedNode != null) {
          graph.addEdge(virtualRoot, isolatedNode);
          debugPrint('✅ Arête virtuelle ajoutée: Root -> ${isolatedId}');
        }
      }
      
      // 🔑 Si des nœuds sont isolés, utiliser FruchtermanReingold pour un meilleur layout
      // FruchtermanReingold gère mieux les graphes avec plusieurs composantes connexes
      _useFruchtermanReingold = true;
      debugPrint('🔄 Utilisation de FruchtermanReingoldAlgorithm pour un meilleur layout (nœuds isolés détectés)');
      
      // 🔑 Si des nœuds sont isolés, utiliser FruchtermanReingold pour un meilleur layout
      // FruchtermanReingold gère mieux les graphes avec plusieurs composantes connexes
      _useFruchtermanReingold = true;
      debugPrint('🔄 Utilisation de FruchtermanReingoldAlgorithm pour un meilleur layout (nœuds isolés détectés)');
    }
  }

  void _addMemberAndChildrenToGraph(Membre? parent, Membre currentMember) {
    // 1. Créer le nœud du membre actuel ou le récupérer s'il existe (pour éviter les doublons dans les relations multiples)
    Node currentNode;

    if (_memberNodes.containsKey(currentMember.id)) {
      currentNode = _memberNodes[currentMember.id]!;
      debugPrint('⚠️ Nœud déjà existant pour ${currentMember.nomComplet} (ID: ${currentMember.id})');
    } else {
      // Le widget de la carte du membre sera le contenu du nœud
      currentNode = Node.Id(currentMember.id);
      graph.addNode(currentNode);
      _memberNodes[currentMember.id] = currentNode;
      debugPrint('✅ Nœud ajouté: ${currentMember.nomComplet} (ID: ${currentMember.id})');
    }

    // 2. Ajouter l'arête (connexion) avec le parent
    if (parent != null) {
      // Assurer que le nœud parent existe
      Node? parentNode = _memberNodes[parent.id];
      if (parentNode != null) {
        // 🔑 CORRECTION: Vérifier si l'arête n'existe pas déjà
        bool edgeExists = graph.edges.any((edge) =>
            (edge.source == parentNode && edge.destination == currentNode) ||
            (edge.source == currentNode && edge.destination == parentNode));
        
        if (!edgeExists) {
          graph.addEdge(parentNode, currentNode);
          debugPrint('✅ Arête ajoutée: ${parent.nomComplet} -> ${currentMember.nomComplet}');
        } else {
          debugPrint('⚠️ Arête déjà existante: ${parent.nomComplet} -> ${currentMember.nomComplet}');
        }
      } else {
        debugPrint('⚠️ Nœud parent non trouvé pour ${currentMember.nomComplet}');
      }
    }

    // 3. Appeler récursivement pour les enfants (pour construire la hiérarchie)
    debugPrint('📋 ${currentMember.nomComplet} a ${currentMember.enfants.length} enfant(s)');
    for (var enfant in currentMember.enfants) {
      _addMemberAndChildrenToGraph(currentMember, enfant);
    }
  }

  // 1. Gère l'ajout d'un nouveau membre (appelé par le FAB ou le Placeholder)
  void _navigateToAddMember({int? parentId}) async {
    final bool? shouldRefresh = await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => CreateTreeScreen(
          familyId: widget.familyId,
          parentId: parentId, // ID du parent passé
        ),
      ),
    );

    if (shouldRefresh == true) {
      // Pour forcer le rafraîchissement des données après l'ajout
      await _fetchFamilyTree();
    }
  }

  // 2. Gère l'affichage des détails d'un membre (appelé par la carte du membre)
  void _navigateToMemberDetail({required int memberId}) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => MembreDetailScreen(membreId: memberId),
      ),
    );
  }

  // 3. Gestionnaire de clic UNIFIÉ pour les cartes
  void _handleMemberCardTap({required int? memberId, bool isPlaceholder = false}) {
    if (memberId == null || isPlaceholder) {
      // Si l'ID est null (placeholder d'ajout)
      _navigateToAddMember(parentId: memberId); // Si l'ID est null, ajoute un membre principal
    } else {
      // Si un ID est présent, affiche les détails
      _navigateToMemberDetail(memberId: memberId);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: _backgroundColor,
      drawer: AppDrawer(familyId: widget.familyId),
      body: SingleChildScrollView(
        padding: EdgeInsets.only(
          top: MediaQuery.of(context).padding.top + 10,
          bottom: 20,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Builder(
              builder: (BuildContext innerContext) {
                return _buildCustomHeader(innerContext);
              },
            ),
            const SizedBox(height: 10),
            _buildContent(),
          ],
        ),
      ),
      // FloatingActionButton rétabli (pour ajouter un membre principal)
      floatingActionButton: FloatingActionButton(
        // L'action du FAB appelle la fonction d'ajout directe
        onPressed: () => _navigateToAddMember(parentId: null),
        backgroundColor: _mainAccentColor,
        child: const Icon(Icons.person_add, color: Colors.white),
        tooltip: 'Ajouter un nouveau membre',
      ),
    );
  }

  // -------------------------------------------------------------------
  // --- STRUCTURE DE LA PAGE (Dynamique) ---
  // -------------------------------------------------------------------

  Widget _buildContent() {
    if (_isLoading) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(50.0),
          child: Column(
            children: [
              const CircularProgressIndicator(color: _mainAccentColor),
              const SizedBox(height: 10),
              Text(
                'Chargement de l\'arbre généalogique...',
                style: TextStyle(color: Colors.grey.shade600),
              ),
            ],
          ),
        ),
      );
    }

    if (_errorMessage != null) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(30.0),
          child: Column(
            children: [
              const Icon(Icons.error_outline, color: Colors.red, size: 40),
              const SizedBox(height: 10),
              Text(
                _errorMessage!,
                textAlign: TextAlign.center,
                style: const TextStyle(color: Colors.red),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _fetchFamilyTree,
                style: ElevatedButton.styleFrom(backgroundColor: _mainAccentColor),
                child: const Text('Réessayer', style: TextStyle(color: Colors.white)),
              )
            ],
          ),
        ),
      );
    }

    if (_familleData != null) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildStats(),
          const SizedBox(height: 30),
          // 🔑 NOUVEL AFFICHAGE: Utilisation de GraphView avec taille dynamique
          _buildGraphViewLayout(),
          const SizedBox(height: 20),
        ],
      );
    }

    return const Center(child: Text("Aucune donnée disponible."));
  }

  // 🔑 WIDGET GRAPHVIEW CORRIGÉ
  Widget _buildGraphViewLayout() {
    // 🔑 CORRECTION: Utilisation de 'membres'
    if (_familleData == null || _familleData!.membres.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: _buildMemberCardPlaceholder(
            onTap: (id) => _handleMemberCardTap(memberId: id, isPlaceholder: true),
          ),
        ),
      );
    }

    // 🔑 DEBUG: Vérifier l'état du graphe
    debugPrint('--- État du graphe au rendu ---');
    debugPrint('Nombre de nœuds: ${graph.nodes.length}');
    debugPrint('Nombre d\'arêtes: ${graph.edges.length}');
    debugPrint('Dimensions: ${_graphWidth}x${_graphHeight}');
    debugPrint('--------------------------------');

    // 🔑 CORRECTION: Si le graphe est vide, afficher un message
    if (graph.nodes.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.warning, color: Colors.orange, size: 48),
              const SizedBox(height: 16),
              const Text(
                'Aucun nœud dans le graphe',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              Text(
                'Nœuds: ${graph.nodes.length}, Arêtes: ${graph.edges.length}',
                style: const TextStyle(fontSize: 12, color: Colors.grey),
              ),
            ],
          ),
        ),
      );
    }
    
    // 🔑 CORRECTION CRITIQUE: Utiliser OverflowBox pour permettre au GraphView de déterminer sa propre taille
    // Ne pas imposer de contraintes fixes qui causent des conflits
    final double containerWidth = _graphWidth > 0 ? _graphWidth : 1200.0;
    final double containerHeight = _graphHeight > 0 ? _graphHeight : 1000.0;
    
    return Container(
      // 🔑 NOUVEAU: Utiliser une hauteur basée sur l'écran
      height: MediaQuery.of(context).size.height * 0.7,
      width: double.infinity,
      color: _lightCardColor.withOpacity(0.3),
      child: InteractiveViewer(
        // 🔑 CORRECTION : Utilisation du contrôleur pour la vue initiale
        transformationController: _transformationController,
        constrained: false, // Permet le défilement et le zoom
        boundaryMargin: const EdgeInsets.all(200), // 🔑 AUGMENTÉ: Plus de marge pour le scroll
        minScale: 0.05, // 🔑 RÉDUIT pour permettre un zoom arrière très important
        maxScale: 5.0, // 🔑 AUGMENTÉ pour permettre un zoom avant très important
        panEnabled: true, // 🔑 Permet le pan (déplacement)
        scaleEnabled: true, // 🔑 Permet le zoom (pincement)
        child: UnconstrainedBox(
          // 🔑 CORRECTION CRITIQUE: UnconstrainedBox permet au GraphView de déterminer sa propre taille
          // Le GraphView calculera sa taille basée sur le layout algorithm sans contraintes strictes
          alignment: Alignment.topLeft,
          child: GraphView(
            graph: graph,
            algorithm: _useFruchtermanReingold
                ? FruchtermanReingoldAlgorithm(frConfig)
                : BuchheimWalkerAlgorithm(builder, TreeEdgeRenderer(builder)),
            builder: (Node node) {
              // Convertir l'ID du nœud en Membre
              final int? memberId = node.key!.value as int?;

              // 🔑 CORRECTION: Ignorer le nœud virtuel (ID négatif)
              if (memberId != null && memberId < 0) {
                return const SizedBox.shrink(); // Ne pas afficher le nœud racine virtuel
              }

              // Trouver le Membre correspondant dans les racines ou enfants
              // 🔑 CORRECTION: Utilisation de 'membres'
              Membre? member = _findMemberInHierarchicalData(memberId, _familleData!.membres);

              // 🔑 Le nœud du graphe contient la carte du membre
              if (member != null) {
                final MemberTapCallback cardTap = (id) => _handleMemberCardTap(memberId: id);

                return _buildMemberCardDynamic(member, onTap: cardTap, isGraphNode: true);
              } else {
                // Devrait être uniquement le cas pour le nœud racine initial s'il n'est pas un vrai membre
                debugPrint('⚠️ Membre non trouvé pour le nœud ID: $memberId');
                return const SizedBox.shrink();
              }
            },
          ),
        ),
      ),
    );
  }

  // Fonction utilitaire pour trouver un membre par ID dans la structure récursive
  Membre? _findMemberInHierarchicalData(int? id, List<Membre> membres) {
    if (id == null) return null;

    for (var membre in membres) {
      if (membre.id == id) {
        return membre;
      }

      // Recherche récursive dans les enfants
      final foundInChild = _findMemberInHierarchicalData(id, membre.enfants);
      if (foundInChild != null) {
        return foundInChild;
      }
    }

    return null;
  }

  // -------------------------------------------------------------------
  // --- WIDGETS DE L'ARBRE (Adaptés pour GraphView) ---
  // -------------------------------------------------------------------

  // *Carte de membre dynamique* (Refonte style MyHeritage pour GraphView)
  Widget _buildMemberCardDynamic(
    Membre? membre, {
    required MemberTapCallback onTap,
    required bool isGraphNode,
  }) {
    if (membre == null) {
      if (isGraphNode) return const SizedBox.shrink();

      return _buildMemberCardPlaceholder(
        onTap: (id) => _handleMemberCardTap(memberId: id, isPlaceholder: true),
      );
    }

    // --- Configuration de la carte ---
    const double cardWidth = 150; // Largeur légèrement réduite pour un look plus compact
    const double cardHeight = 150; // Hauteur fixe pour forcer le rendu dans GraphView

    // Construction de l'URL complète pour la photo
    // 🔑 ADAPTATION: Le chemin peut être soit "images/uuid.jpg" (ancien format) soit "/uploads/images/uuid.jpg" (nouveau format)
    final String fullPhotoUrl = (membre.photoUrl != null && membre.photoUrl!.isNotEmpty)
        ? membre.photoUrl!.startsWith('/uploads/')
            ? '$_baseUrl${membre.photoUrl!}' // Format nouveau: /uploads/images/uuid.jpg
            : '$_baseUrl/${membre.photoUrl!}' // Format ancien: images/uuid.jpg
        : '';

    final bool hasPhoto = fullPhotoUrl.isNotEmpty;

    // --- Extraction des données ---
    String displayName = membre.nomComplet ?? 'Membre inconnu';
    String relation = membre.relationFamiliale ?? '';

    return GestureDetector(
      onTap: () => onTap(membre.id),
      // 🔑 CORRECTION CRITIQUE: Utilisation de SizedBox pour définir la taille du nœud dans le graphe
      child: SizedBox(
        width: cardWidth,
        height: cardHeight,
        child: Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(15), // Bords plus arrondis (plus moderne)
            border: Border.all(color: _mainAccentColor.withOpacity(0.5), width: 2), // Bordure accentuée
            boxShadow: [
              BoxShadow(
                color: Colors.grey.withOpacity(0.2),
                spreadRadius: 2,
                blurRadius: 5,
                offset: const Offset(0, 3),
              ),
            ],
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // 1. Zone de la Photo/Avatar (70% de la hauteur)
              Expanded(
                flex: 3,
                child: ClipRRect(
                  borderRadius: const BorderRadius.only(
                    topLeft: Radius.circular(13),
                    topRight: Radius.circular(13),
                  ),
                  child: Container(
                    color: _lightCardColor, // Couleur de fond pour l'avatar
                    child: hasPhoto
                        ? Image.network(
                            fullPhotoUrl,
                            fit: BoxFit.cover,
                            errorBuilder: (context, error, stackTrace) => Center(
                              child: Icon(
                                Icons.person,
                                color: _mainAccentColor.withOpacity(0.7),
                                size: 40,
                              ),
                            ),
                          )
                        : Center(
                            child: Icon(
                              Icons.person,
                              color: _mainAccentColor.withOpacity(0.7),
                              size: 40,
                            ),
                          ),
                  ),
                ),
              ),
              // 2. Zone du Texte (30% de la hauteur)
              Expanded(
                flex: 2,
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 5.0, vertical: 3.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        displayName,
                        textAlign: TextAlign.center,
                        style: const TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 12,
                          color: _cardTextColor,
                        ),
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                      ),
                      if (relation.isNotEmpty)
                        Text(
                          relation,
                          style: const TextStyle(fontSize: 9, color: Colors.grey),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  // Carte de membre POUBELLE (Placeholder) (Inchangée)
  Widget _buildMemberCardPlaceholder({required MemberTapCallback onTap}) {
    return GestureDetector(
      onTap: () => _handleMemberCardTap(memberId: null, isPlaceholder: true),
      child: Container(
        margin: const EdgeInsets.only(bottom: 10),
        padding: const EdgeInsets.all(10),
        decoration: BoxDecoration(
          color: _lightCardColor,
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: Colors.grey.shade400, width: 1.5),
        ),
        child: Row(
          children: [
            const Icon(Icons.add_circle_outline, color: _mainAccentColor, size: 30),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: const [
                  Text(
                    'Ajouter un Membre',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 14,
                      color: _mainAccentColor,
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                  Text(
                    'Cliquez ici pour remplir cette place.',
                    style: TextStyle(fontSize: 10, color: Colors.grey),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // -------------------------------------------------------------------
  // --- WIDGETS DE STYLE ET UTILS (Adaptation du StatBox) ---
  // -------------------------------------------------------------------

  // Section des statistiques
  Widget _buildStats() {
    final famille = _familleData;

    if (famille == null) return const SizedBox.shrink();

    int? anneeDebut;

    // 🔑 CORRECTION: Utilisation de 'membres'
    if (famille.membres.isNotEmpty) {
      try {
        // Utilise l'opérateur de null-safe ? puisque dateNaissance est optionnel
        String? date = famille.membres.first.dateNaissance;

        if (date != null && date.contains('-')) {
          anneeDebut = int.tryParse(date.split('-').first);
        }
      } catch (_) {
        anneeDebut = null;
      }
    }

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Arbre Généalogique de ${famille.nomFamille}',
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: _cardTextColor,
            ),
          ),
          // Utilise l'opérateur ?? '' pour gérer le cas où description est null
          Text(
            (famille.description ?? '').isEmpty ? 'Arbre de la famille' : famille.description!,
            style: const TextStyle(fontSize: 12, color: Colors.grey),
          ),
          const SizedBox(height: 15),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              _buildStatBox(
                label: 'Générations',
                value: _maxLevel > 0 ? '${_maxLevel + 1}' : 'N/A',
              ),
              _buildStatBox(
                label: 'Membres',
                value: famille.nombreMembres?.toString() ?? '0',
              ),
              _buildStatBox(
                label: 'Depuis',
                value: anneeDebut?.toString() ?? '...',
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildCustomHeader(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          IconButton(
            icon: const Icon(Icons.menu, color: _cardTextColor, size: 30),
            onPressed: () {
              Scaffold.of(context).openDrawer();
            },
          ),
          const Text(
            'Héritage Numérique',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: _cardTextColor,
            ),
          ),
          IconButton(
            icon: const Icon(Icons.close, color: Colors.grey),
            onPressed: () => Navigator.pop(context),
          ),
        ],
      ),
    );
  }

  Widget _buildStatBox({required String label, required String value}) {
    return Container(
      width: 80,
      height: 60,
      decoration: BoxDecoration(
        color: _lightCardColor,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            value,
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: _mainAccentColor,
            ),
          ),
          Text(
            label,
            style: const TextStyle(
              fontSize: 10,
              color: _cardTextColor,
            ),
          ),
        ],
      ),
    );
  }
}

