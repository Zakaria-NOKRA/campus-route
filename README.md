# Campus Route

Application d'orientation **à l'intérieur d'un campus universitaire** (Université Sorbonne Paris Nord).
Les applications de cartographie classiques s'arrêtent à l'entrée du campus : Campus Route prend le relais et guide
l'utilisateur jusqu'à la porte de la bonne salle, comme un GPS d'intérieur.

Les données actuelles représentent un bâtiment exemple (bâtiment B, 3 étages, 27 salles).

## Fonctionnalités

- **Recherche de salle** : trouver une salle par son code, son nom ou son type (par exemple `B204`, `amphi`, `labo`).
- **Fiche d'une salle** : étage, capacité et équipements disponibles.
- **Calcul d'itinéraire** : chemin le plus court entre deux points du bâtiment, avec la distance, la durée estimée et
  des instructions pas à pas en français (« Tournez à droite et avancez de 6 m »).
- **Itinéraire accessible** : option pour éviter les escaliers et passer par l'ascenseur, pour les personnes à
  mobilité réduite.
- **Plan du bâtiment** : toutes les données nécessaires pour dessiner chaque étage et tracer le trajet.
- **Documentation interactive** : une page Swagger pour découvrir et tester l'API.

## Technologies

- Back-end : Java 17, Spring Boot, Spring Data JPA, Maven
- Base de données : PostgreSQL (lancée avec Docker)
- Front-end : React
- Documentation de l'API : OpenAPI / Swagger
- Tests de l'API : Postman

## Prérequis

| Outil | Version | Vérifier |
|---|---|---|
| Git | récent | `git --version` |
| JDK (Java) | 17 ou plus | `java -version` |
| Docker Desktop | récent | `docker --version` |

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
le message `Started CampusRouteBackendApplication` apparaît. Les données du bâtiment sont chargées automatiquement au
premier démarrage.

**4. Vérifier**

Ouvre la documentation de l'API (voir la section suivante) : si la page s'affiche, tout fonctionne.

**Arrêter le projet** : `Ctrl+C` dans le terminal du back-end, puis `docker compose down` à la racine du dépôt.

Problèmes fréquents :

| Problème | Solution |
|---|---|
| `Cannot connect to the Docker daemon` | Docker Desktop n'est pas démarré. |
| Port 5432 ou 8080 déjà utilisé | Une autre application utilise ce port : ferme-la, ou change le port. |
| Erreur de compilation Java | Vérifie que `java -version` indique 17 ou plus. |

## Documentation de l'API (Swagger)

Quand le back-end tourne, la documentation interactive est disponible à l'adresse :

**http://localhost:8080/swagger-ui.html**

Elle présente tous les endpoints (recherche de salle, fiche d'une salle, itinéraire, plan du bâtiment), leurs
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

## Architecture

```
campus-route/
├── backend/              API Spring Boot
├── frontend/             application React
├── prototype/            prototype HTML/JS de référence
├── docs/                 documents du projet
├── postman/              collection Postman
├── docker-compose.yml    base de données PostgreSQL pour le développement
└── README.md
```

Le back-end est organisé en couches, dans `backend/src/main/java/com/campusroute/campus_route_backend/` :

| Package | Rôle |
|---|---|
| `controller` | reçoit les requêtes HTTP et appelle les services |
| `service` | logique métier : recherche, calcul d'itinéraire, instructions, plan |
| `repository` | accès à la base de données |
| `domain` | entités : salles, nœuds et arêtes du graphe du bâtiment |
| `dto` | objets renvoyés par l'API |
| `config` | configuration : chargement des données, CORS, documentation |

Le bâtiment est représenté par un **graphe** : les nœuds sont les salles, les couloirs, l'escalier, l'ascenseur et
l'entrée, et les arêtes sont les trajets possibles entre eux. Le plus court chemin est calculé avec l'algorithme de
Dijkstra.

## Contribuer

- La branche `main` est stable : on n'y pousse pas directement.
- Une branche par tâche (`feature/<sujet>`), puis une Pull Request vers `main`.
- Messages de commit au format `feat:`, `fix:`, `docs:`, `test:` ou `chore:`, suivis d'une phrase claire.
- Avant une Pull Request, les tests doivent passer.

## Équipe

Projet réalisé dans le cadre du cours de méthodologies agiles et langage avancé (Master 2, Université Sorbonne Paris
Nord), selon la méthode Scrum.

- **Nidal** : coordination, Git, déploiement
- **Wiame** : maquettes et front-end
- **Zakaria** : back-end, base de données, calcul d'itinéraire