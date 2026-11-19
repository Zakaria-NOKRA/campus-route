# Campus Route

Application d'orientation **à l'intérieur d'un campus universitaire** (Université Sorbonne Paris Nord).
Les applications de cartographie classiques s'arrêtent à l'entrée du campus : Campus Route prend le relais et guide
l'utilisateur jusqu'à la porte de la bonne salle, comme un GPS d'intérieur.

Les données actuelles représentent un campus fictif : trois bâtiments (A, B, C) reliés par une cour, 77 salles en
tout (voir [Données du campus](#données-du-campus)).

## Fonctionnalités

- **Recherche de salle** : trouver une salle par son code, son nom ou son type (par exemple `A204`, `amphi`, `labo`).
- **Fiche d'une salle** : bâtiment, étage, capacité et équipements disponibles.
- **Calcul d'itinéraire** : chemin le plus court entre deux points du campus, même d'un bâtiment à l'autre, avec la
  distance, la durée estimée et des instructions pas à pas en français (« Tournez à droite et avancez de 6 m »,
  « Sortez du bâtiment A », « Entrez dans le bâtiment B »).
- **Itinéraire accessible** : option pour éviter les escaliers (ascenseur à l'intérieur, rampe à l'extérieur), pour
  les personnes à mobilité réduite.
- **Plan interactif** : affichage de chaque étage, sélection d'une salle sur le plan ou dans la recherche, et tracé de l'itinéraire avec ses étapes.
- **Documentation interactive** : une page Swagger pour découvrir et tester l'API.

## Technologies

- Back-end : Java 17, Spring Boot, Spring Data JPA, Maven
- Base de données : PostgreSQL (lancée avec Docker)
- Front-end : React, Vite
- Documentation de l'API : OpenAPI / Swagger
- Tests de l'API : Postman

## Prérequis

| Outil          | Version    | Vérifier           |
| -------------- | ---------- | ------------------ |
| Git            | récent     | `git --version`    |
| JDK (Java)     | 17 ou plus | `java -version`    |
| Docker Desktop | récent     | `docker --version` |
| Node.js        | 22 ou plus | `node -v`          |

- Maven n'est pas à installer : le projet embarque son propre Maven (`mvnw`).
- Docker Desktop doit être **démarré** avant de lancer la base de données.

## Lancer le projet

Commandes pour **Windows (PowerShell)**. Sous macOS ou Linux, remplace `.\mvnw.cmd` par `./mvnw`.

**1. Récupérer le code**

```powershell
git clone <url-du-depot>
cd campus-route
```

**2. Démarrer la base de données** (depuis la racine du dépôt)

```powershell
docker compose up -d
```

**3. Démarrer le back-end**

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Le premier lancement télécharge les dépendances, ce qui peut prendre quelques minutes. Le démarrage est terminé quand
le message `Started CampusRouteBackendApplication` apparaît. Les données du campus sont chargées automatiquement au
premier démarrage, quand la base est vide (si ta base date d'une version précédente, voir
[Mettre à jour une base existante](#mettre-à-jour-une-base-existante)).

**4. Démarrer le front-end** (dans un deuxième terminal, depuis la racine du dépôt)

```powershell
cd frontend
npm install
npm run dev
```

Ouvre ensuite **http://localhost:5173** dans ton navigateur : l'application s'affiche. Le back-end doit être démarré pour que la recherche et les itinéraires fonctionnent.

**5. Vérifier**

Cherche une salle (par exemple `A204`), sélectionne-la, puis clique sur « Calculer l'itinéraire » : le trajet s'affiche sur le plan. La documentation de l'API est aussi disponible (voir la section suivante).

**Arrêter le projet** : `Ctrl+C` dans chaque terminal (back-end et front-end), puis `docker compose down` à la racine du dépôt.

Problèmes fréquents :

| Problème                                               | Solution                                                                                         |
| ------------------------------------------------------ | ------------------------------------------------------------------------------------------------ |
| `Cannot connect to the Docker daemon`                  | Docker Desktop n'est pas démarré.                                                                |
| Port 5432, 8080 ou 5173 déjà utilisé                   | Une autre application utilise ce port : ferme-la, ou change le port.                             |
| Erreur de compilation Java                             | Vérifie que `java -version` indique 17 ou plus.                                                  |
| `npm` ou `node` introuvable                            | Node.js n'est pas installé : installe la version LTS depuis nodejs.org, puis rouvre le terminal. |
| La page s'affiche mais la recherche renvoie une erreur | Le back-end n'est pas démarré (étape 3).                                                         |
| Le back-end affiche `The database holds old data`      | La base date d'une version précédente : voir [Mettre à jour une base existante](#mettre-à-jour-une-base-existante). |

## Documentation de l'API (Swagger)

Quand le back-end tourne, la documentation interactive est disponible à l'adresse :

**http://localhost:8080/swagger-ui.html**

Elle présente tous les endpoints (recherche de salle, fiche d'une salle, itinéraire, liste des bâtiments, plan d'un
bâtiment), leurs
paramètres, des exemples de réponses, et permet de les essayer directement depuis le navigateur avec le bouton
« Try it out ».

Le contrat de l'API au format OpenAPI est aussi disponible à l'adresse `http://localhost:8080/v3/api-docs`.

## Tester avec Postman

La collection **Campus Route API** est versionnée dans le dossier [`postman/`](postman/). Elle regroupe des requêtes
pour chaque fonctionnalité, avec des vérifications automatiques des réponses.

1. Ouvre le dossier du dépôt dans Postman : la collection apparaît dans l'onglet « Collections ».
2. Utilise la variable `baseUrl` avec la valeur `http://localhost:8080` (dans un environnement Postman).
3. Démarre le back-end, ouvre une requête, clique sur **Send**, puis consulte l'onglet **Test Results**.

## Tests automatiques

```powershell
cd backend
.\mvnw.cmd test
```

Les tests s'exécutent sans Docker et doivent se terminer par `BUILD SUCCESS`.

Pour vérifier le front-end :

```powershell
cd frontend
npm run lint
npm run build
```

Ces deux vérifications sont aussi exécutées automatiquement à chaque Pull Request.

## Architecture

```
campus-route/
├── backend/              API Spring Boot
├── frontend/             application React
├── prototype/            prototype HTML/JS de référence
├── docs/                 documents du projet
├── postman/              collection Postman
├── tools/campus/         générateur des données du campus (Node, sans dépendance)
├── docker-compose.yml    base de données PostgreSQL pour le développement
└── README.md
```

Le back-end est organisé en couches, dans `backend/src/main/java/com/campusroute/campus_route_backend/` :

| Package      | Rôle                                                                |
| ------------ | ------------------------------------------------------------------- |
| `controller` | reçoit les requêtes HTTP et appelle les services                    |
| `service`    | logique métier : recherche, calcul d'itinéraire, instructions, plan |
| `repository` | accès à la base de données                                          |
| `domain`     | entités : bâtiments, salles, nœuds et arêtes du graphe du campus    |
| `dto`        | objets renvoyés par l'API                                           |
| `config`     | configuration : chargement des données, CORS, documentation         |

Le campus est représenté par un **graphe** : les nœuds sont les portes des salles, les points des couloirs, les
escaliers, les ascenseurs, les portes des bâtiments et les points de la cour, et les arêtes sont les trajets possibles
entre eux. Le plus court chemin est calculé avec l'algorithme de Dijkstra.

## Données du campus

Le campus est fictif. L'échelle des plans est de 1 pixel = 0,07 m.

| Bâtiment | Rôle                            | Forme                       | Étages    | Salles |
| -------- | ------------------------------- | --------------------------- | --------- | ------ |
| A        | Sciences                        | en L, deux ailes            | RDC, 1, 2 | 32     |
| B        | Amphithéâtres et administration | rectangle avec hall central | RDC, 1    | 16     |
| C        | Bibliothèque / learning center  | en U autour d'un atrium     | RDC, 1, 2 | 29     |

- Le code d'une salle est la lettre du bâtiment suivie de 3 chiffres, le premier étant l'étage : `A204` est au
  2e étage du bâtiment A.
- Chaque bâtiment a deux escaliers et un ascenseur. Dans le bâtiment C, l'ascenseur est au bout de l'aile ouest :
  un itinéraire accessible vers l'aile est est nettement plus long.
- La cour relie les bâtiments depuis l'entrée du campus (`ENTREE`). Le bâtiment C est sur une terrasse : on y accède
  par des marches, ou par une rampe plus longue pour un itinéraire accessible.
- Chaque bâtiment a son propre plan ; la cour a le sien (code `EXT`).

Dans le front-end, des onglets permettent de choisir un bâtiment ou l'extérieur, puis l'étage. Le plan de l'extérieur
montre les bâtiments (cliquables), les allées, les marches et la rampe. Un itinéraire est tracé bâtiment par bâtiment
et étage par étage ; les onglets traversés sont marqués d'un point.

### Modifier ou régénérer les données

Le fichier `backend/src/main/resources/data/campus_route_graph.json` est **généré** : ne le modifie pas à la main.
La source est `tools/campus/campus.source.mjs` (salles en rectangles, couloirs en lignes brisées, escaliers,
ascenseurs, portes, points de la cour). Depuis la racine du dépôt :

```powershell
node tools/campus/generate.mjs           # valide le graphe puis réécrit le fichier JSON
node tools/campus/generate.mjs --check   # vérifie seulement que le JSON est à jour
node --test tools/campus/generate.test.mjs
```

Le script vérifie notamment que les identifiants sont uniques, que chaque salle a une porte reliée à un couloir, que
les étages sont reliés par escalier et ascenseur, qu'aucun nœud n'est isolé, que tout le campus est accessible
depuis l'entrée, et que chaque salle est accessible sans escalier. En cas d'erreur, il les liste toutes et n'écrit
rien.

Pour voir les plans générés, ajoute `--preview <dossier>` avec un dossier **hors du dépôt** (par exemple
`--preview "$env:TEMP\campus-preview"`) : un fichier SVG par bâtiment et par étage y est écrit.

### Mettre à jour une base existante

Le back-end ne charge les données que si la base est vide. Après un changement des données (ou si ta base date de
la version à un seul bâtiment), vide la base locale puis redémarre le back-end :

```powershell
docker compose down -v
docker compose up -d
```

`-v` supprime le volume de la base PostgreSQL de développement : toutes ses données sont effacées, puis rechargées
depuis le fichier JSON au démarrage suivant. Le journal du back-end indique alors
`Graph imported: 4 buildings, 77 rooms, 251 nodes, 264 edges`.

## Contribuer

- La branche `main` est stable : on n'y pousse pas directement.
- Une branche par tâche (`feature/<sujet>`), puis une Pull Request vers `main`.
- Messages de commit au format `feat:`, `fix:`, `docs:`, `test:` ou `chore:`, suivis d'une phrase claire.
- Avant une Pull Request, les tests du back-end et les vérifications du front-end (`lint` et `build`) doivent passer.

## Équipe

Projet réalisé dans le cadre du cours de méthodologies agiles et langage avancé (Master 2, Université Sorbonne Paris
Nord), selon la méthode Scrum.

- **Nidal** : développement front-end, création de la carte du campus et du guide utilisateur, coordination de la démarche agile
- **Wiame** : conception, maquettes et cahier des charges, développement front-end
- **Zakaria** : back-end, base de données et moteur d'itinéraire
