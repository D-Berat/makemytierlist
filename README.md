# MakeMyTierlist

**Une application de bureau pour créer, personnaliser et sauvegarder ses tier lists.**

Projet collectif réalisé à quatre dans le cadre de la **SAÉ 2.01 — Développement d'une application**, en première année de BUT Informatique à l'IUT de Laval.

Java 21 · JavaFX 21 · FXML / CSS · Maven · Jackson · MVC

![Éditeur de tier list](docs/images/editeur.png)

*Capture extraite du rapport de projet fourni avec le livrable.*

## Fonctionnalités

- Créer, renommer, dupliquer et supprimer plusieurs tier lists.
- Ajouter des éléments textuels ou des images locales.
- Classer et réordonner les éléments par **glisser-déposer** ; réordonner les catégories.
- Personnaliser le nom, la couleur et la hauteur des catégories, ainsi que la taille des éléments.
- Réinitialiser un classement en ramenant les éléments dans la zone « À classer ».
- Sauvegarder à la fermeture et restaurer au démarrage par **sérialisation binaire Java**.
- Importer et exporter une tier list au format `.tl`.
- Rechercher des images de jeux vidéo via l'**API RAWG**, avec une clé personnelle.

![Accueil et gestion des tier lists](docs/images/accueil.png)

## Ce que ce projet met en pratique

| Domaine | Réalisation dans le code |
| --- | --- |
| Programmation orientée objet | Hiérarchie `Item`, `TextItem`, `ImageItem` ; composition des classements et catégories |
| Architecture MVC | Modèle métier séparé des contrôleurs JavaFX et des vues FXML |
| Persistance | `Serializable`, flux objets, duplication par copie profonde et restauration du compteur d'identifiants |
| Interface événementielle | Dialogues, menus contextuels, glisser-déposer et personnalisation visuelle |
| API HTTP et JSON | `HttpClient`, encodage des recherches, conversion JSON en objets avec Jackson |
| Travail collectif | Conception de l'IHM, développement en groupe et utilisation de Git |

## Lancer l'application

### Prérequis

- Un **JDK 21** avec `JAVA_HOME` correctement configuré (`java -version`).
- Une connexion Internet pour le premier téléchargement des dépendances Maven.
- Un environnement graphique de bureau.

JavaFX et Jackson sont récupérés par Maven ; il n'est pas nécessaire d'installer séparément le SDK JavaFX.

```bash
git clone https://github.com/D-Berat/makemytierlist.git
cd makemytierlist
```

**Windows — PowerShell :**

```powershell
.\mvnw.cmd clean javafx:run
```

**Linux / macOS :**

```bash
sh ./mvnw clean javafx:run
```

### Recherche RAWG (facultative)

La création de listes, les textes et les images locales fonctionnent sans clé API. Pour la recherche en ligne, définir `RAWG_API_KEY` dans le terminal de lancement :

```powershell
# Windows PowerShell
$env:RAWG_API_KEY = "votre_cle_personnelle"
.\mvnw.cmd javafx:run
```

```bash
# Linux / macOS
export RAWG_API_KEY="votre_cle_personnelle"
sh ./mvnw javafx:run
```

Un fichier `.env` n'est pas chargé automatiquement. La clé n'est pas fournie dans ce dépôt.

### Compiler

```powershell
.\mvnw.cmd clean verify
```

Sur Linux / macOS : `sh ./mvnw clean verify`. Le JAR assemblé est créé dans `target/SAE_TIERLIST-1.0-SNAPSHOT.jar` et se lance avec `java -jar target/SAE_TIERLIST-1.0-SNAPSHOT.jar`. Il dépend des bibliothèques natives JavaFX de la plateforme de compilation : ce n'est pas un exécutable autonome universel.

Compilation et assemblage vérifiés sous Windows avec le JDK 21 (`clean verify`). Les interactions graphiques et la recherche RAWG n'ont pas été retestées lors de la préparation de ce dépôt.

## Architecture

```text
src/main/java/com/example/sae_tierlist/
├── MainApplication.java       # Démarrage, chargement et sauvegarde à la fermeture
├── Launcher.java              # Point d'entrée du JAR assemblé
├── model/                     # TierListManager, TierList, Tier et hiérarchie Item
├── controller/                # Accueil, éditeur et dialogues JavaFX
├── persistence/               # Sauvegarde, restauration, import et export binaires
└── api/                       # Client RAWG et objets de transfert JSON
src/main/resources/
├── com/example/sae_tierlist/   # Vues FXML et feuille de style
└── image/                     # Ressources graphiques de l'application
```

## Données et limites connues

- `sauvegarde.bin` est créé dans le répertoire de lancement. Les sauvegardes personnelles et les exports sont exclus de Git.
- Les images locales sont référencées par leur chemin : un export `.tl` n'embarque pas les fichiers images et n'est donc pas entièrement portable entre ordinateurs.
- L'import utilise la désérialisation Java sans filtre : n'importer que des fichiers `.tl` de confiance.
- La recherche HTTP RAWG est synchrone et peut bloquer temporairement l'interface.
- Le thème clair/sombre n'est pas implémenté.
- Le livrable original ne comporte pas de suite de tests automatisés. Une compilation réussie ne constitue pas une validation complète des interactions graphiques.

## Équipe et provenance

**Axel Hamard · Célian Gloro · Berat Dastan · Edgar Bacquaert**

Ce dépôt présente un **travail de groupe** dans le portfolio de Berat Dastan ; les fonctionnalités décrites concernent l'application collective.

### Ma contribution — Berat Dastan

- Développement du **modèle de données** et de la logique métier associée aux tier lists, catégories et éléments.
- Mise en place de la **persistance binaire** pour sauvegarder et restaurer les classements entre les sessions.
- Participation à la conception de l'interface : **maquettes papier et Figma**.
- Contribution au développement de l'IHM en **JavaFX**, avec **Scene Builder**.

Les autres fonctionnalités présentées dans ce README sont des réalisations de l'équipe ; cette section précise mon périmètre personnel.

La version publiée provient du livrable de la SAÉ. La préparation pour GitHub ajoute cette documentation, des captures du rapport, des règles d'exclusion et une configuration de la clé RAWG par variable d'environnement. L'historique public démarre à cet import nettoyé ; il ne reconstitue pas l'historique de développement de l'équipe.
