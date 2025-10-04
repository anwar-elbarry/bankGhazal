# 🏦 Bank Ghazal - Système de Gestion Bancaire

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)
![Status](https://img.shields.io/badge/Status-Active-success.svg)

## 📋 Table des Matières

- [Description](#-description)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration de la Base de Données](#-configuration-de-la-base-de-données)
- [Structure du Projet](#-structure-du-projet)
- [Utilisation](#-utilisation)
- [Modèle de Données](#-modèle-de-données)
- [API et Services](#-api-et-services)
- [Exemples d'Utilisation](#-exemples-dutilisation)
- [Tests](#-tests)
- [Contributeurs](#-contributeurs)

---

## 📖 Description

**Bank Ghazal** est une application de gestion bancaire complète développée en Java, offrant une interface console interactive pour gérer les clients, les comptes bancaires, les transactions et générer des rapports analytiques détaillés.

Le système permet de :
- Gérer les clients et leurs comptes (Courant et Épargne)
- Effectuer des opérations bancaires (versement, retrait, virement)
- Consulter l'historique des transactions
- Générer des analyses et rapports
- Détecter les transactions suspectes
- Recevoir des alertes sur les comptes

---

## ✨ Fonctionnalités

### 👤 Gestion des Clients et Comptes
- ✅ Création de nouveaux clients avec validation email
- ✅ Création de comptes courants (avec découvert autorisé)
- ✅ Création de comptes épargne (avec taux d'intérêt)
- ✅ Consultation de tous les clients
- ✅ Affichage des comptes d'un client

### 💰 Gestion des Transactions
- ✅ **Versement** : Dépôt d'argent sur un compte
- ✅ **Retrait** : Retrait avec validation du découvert
- ✅ **Virement** : Transfert entre deux comptes
- ✅ Enregistrement automatique des transactions
- ✅ Validation des montants et des soldes

### 📜 Historique et Consultation
- ✅ Consultation de l'historique des transactions par compte
- ✅ Tri chronologique des transactions
- ✅ Calcul du total et de la moyenne des transactions
- ✅ Affichage formaté avec date, type, montant et lieu

### 📊 Analyses et Rapports
- ✅ **Top 5 des clients** par solde le plus élevé
- ✅ **Rapport mensuel** : statistiques par type de transaction
- ✅ **Comptes inactifs** : détection des comptes sans activité
- ✅ **Transactions suspectes** : détection basée sur :
  - Montants élevés (> 10 000 €)
  - Fréquence excessive (5+ transactions/heure)
  - Lieux inhabituels

### 🔔 Alertes et Notifications
- ✅ Alerte solde bas (seuil configurable)
- ✅ Alerte inactivité prolongée (nombre de jours configurable)
- ✅ Notifications en temps réel

---

## 🏗️ Architecture

Le projet suit une **architecture en couches** (Layered Architecture) :

```
┌─────────────────────────────────────┐
│     Presentation Layer (UI)         │  ← Interface Console Interactive
├─────────────────────────────────────┤
│     Service Layer (Business)        │  ← Logique métier et validations
├─────────────────────────────────────┤
│     DAO Layer (Data Access)         │  ← Accès aux données
├─────────────────────────────────────┤
│     Entity Layer (Domain)           │  ← Modèle de domaine
├─────────────────────────────────────┤
│     Database (PostgreSQL)           │  ← Persistance des données
└─────────────────────────────────────┘
```

### Principes de Conception
- **Separation of Concerns** : Séparation claire des responsabilités
- **Single Responsibility** : Chaque classe a une responsabilité unique
- **Dependency Injection** : Injection des dépendances via constructeurs
- **Data Access Object (DAO)** : Pattern pour l'accès aux données
- **Service Layer** : Encapsulation de la logique métier

---

## 🔧 Prérequis

- **Java JDK** 17 ou supérieur
- **PostgreSQL** 12 ou supérieur
- **JDBC Driver** pour PostgreSQL
- **IDE** (IntelliJ IDEA, Eclipse, ou VS Code)
- **Git** (optionnel)

---

## 📥 Installation

### 1. Cloner le Projet

```bash
git clone https://github.com/votre-username/bankGhazal.git
cd bankGhazal
```

### 2. Configurer la Base de Données

Créez une base de données PostgreSQL :

```sql
CREATE DATABASE bank_ghazal;
```

### 3. Créer les Tables

Exécutez le script SQL suivant :

```sql
-- Table Client
CREATE TABLE Client (
    id VARCHAR(50) PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- Table Compte
CREATE TABLE Compte (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    numero VARCHAR(20) UNIQUE NOT NULL,
    solde NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    idClient VARCHAR(50) NOT NULL REFERENCES Client(id) ON DELETE CASCADE,
    typeCompte VARCHAR(10) NOT NULL,
    decouvertAutorise NUMERIC(15,2) DEFAULT NULL,
    tauxInteret NUMERIC(5,2) DEFAULT NULL,
);

-- Table Transaction
CREATE TABLE Transaction (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant NUMERIC(15,2) NOT NULL,
    type VARCHAR(50) NOT NULL ,
    lieu VARCHAR(100),
    idCompte VARCHAR(50) NOT NULL REFERENCES Compte(id) ON DELETE CASCADE
);
```

### 4. Configurer la Connexion

Modifiez le fichier `DatabaseConnection.java` avec vos informations :

```java
private static final String URL = "jdbc:postgresql://localhost:5432/bank_ghazal";
private static final String USER = "votre_username";
private static final String PASSWORD = "votre_password";
```

### 5. Ajouter le Driver JDBC

Téléchargez le driver PostgreSQL JDBC depuis [Maven Central](https://mvnrepository.com/artifact/org.postgresql/postgresql) et ajoutez-le au classpath du projet.

### 6. Compiler et Exécuter

```bash
# Compiler
javac -d out src/**/*.java

# Exécuter
java -cp out Presentation.Main
```

---

## 📁 Structure du Projet

```
bankGhazal/
│
├── src/
│   ├── DAO/                          # Data Access Objects
│   │   ├── ClienDAO.java            # Accès aux données clients
│   │   ├── CompteDAO.java           # Accès aux données comptes
│   │   ├── TransactionDAO.java      # Accès aux données transactions
│   │   └── Util/
│   │       └── DatabaseConnection.java  # Gestion connexion DB
│   │
│   ├── Entity/                       # Entités du domaine
│   │   ├── Client.java              # Record Client
│   │   ├── Compte.java              # Classe abstraite Compte (sealed)
│   │   ├── CompteCourant.java       # Compte avec découvert
│   │   ├── CompteEpargne.java       # Compte avec intérêts
│   │   ├── Transaction.java         # Record Transaction
│   │   └── Enum/
│   │       ├── TypeCompte.java      # COURANT, EPARGNE
│   │       └── TypeTransaction.java # VERSEMENT, RETRAIT, VIREMENT
│   │
│   ├── Service/                      # Logique métier
│   │   ├── ClientService.java       # Services clients
│   │   ├── CompteService.java       # Services comptes + opérations bancaires
│   │   ├── TransactionService.java  # Services transactions
│   │   ├── RapportService.java      # Génération de rapports
│   │   └── TransactionFilter.java   # Filtrage des transactions
│   │
│   ├── Presentation/                 # Interface utilisateur
│   │   └── Main.java                # Menu interactif console
│   │
│   └── Utilitaire/                   # Utilitaires
│       └── Validator.java           # Validation email, nom, etc.
│
├── resources/                        # Ressources
├── out/                             # Fichiers compilés
├── .gitignore
├── README.md
└── bankGhazal.iml
```

---

## 🎯 Utilisation

### Lancer l'Application

```bash
java -cp out Presentation.Main
```

### Menu Principal

```
============================================================
🏦  SYSTÈME DE GESTION BANCAIRE - BANK GHAZAL
============================================================
1. 👤 Gestion des Clients et Comptes
2. 💰 Gestion des Transactions
3. 📜 Consulter l'Historique des Transactions
4. 📊 Analyses et Rapports
5. 🔔 Alertes et Notifications
0. 🚪 Quitter
============================================================
```

### Navigation

- Utilisez les **numéros** pour naviguer dans les menus
- Suivez les **instructions** à l'écran
- Tapez **0** pour revenir au menu précédent

---

## 🗄️ Modèle de Données

### Diagramme de Classes (Simplifié)

```
┌─────────────────┐
│     Client      │
├─────────────────┤
│ - id: String    │
│ - nom: String   │
│ - email: String │
└────────┬────────┘
         │ 1
         │
         │ *
┌────────▼────────────────┐
│       Compte            │ (sealed abstract)
├─────────────────────────┤
│ - id: String            │
│ - numero: String        │
│ - solde: BigDecimal     │
│ - idClient: String      │
│ - typeCompte: TypeCompte│
└────────┬────────────────┘
         │
    ┌────┴────┐
    │         │
┌───▼──────┐  ┌───▼──────────┐
│CompteCourant│ │CompteEpargne│
├──────────┤  ├──────────────┤
│-decouvert│  │-tauxInteret  │
└──────────┘  └──────────────┘
    │
    │ 1
    │
    │ *
┌───▼──────────────────┐
│    Transaction       │
├──────────────────────┤
│ - id: String         │
│ - montant: BigDecimal│
│ - date: LocalDateTime│
│ - lieu: String       │
│ - type: TypeTransaction│
│ - idCompte: String   │
└──────────────────────┘
```

### Relations

- **Client** ↔ **Compte** : Un client peut avoir plusieurs comptes (1:N)
- **Compte** ↔ **Transaction** : Un compte peut avoir plusieurs transactions (1:N)
- **Compte** est une classe `sealed` avec deux sous-classes : `CompteCourant` et `CompteEpargne`

---

## 🔌 API et Services

### ClientService

```java
// Créer un client
clientService.addClient(String nom, String email);

// Trouver un client par nom
Optional<Client> client = clientService.findBynome(String nom);

// Lister tous les clients
List<Client> clients = clientService.findAll();
```

### CompteService

```java
// Créer un compte
compteService.addCompte(BigDecimal solde, double decouvert, 
                        double tauxInteret, Client client, TypeCompte type);

// Opérations bancaires
compteService.versement(Compte compte, BigDecimal montant, String lieu);
compteService.retrait(Compte compte, BigDecimal montant, String lieu);
compteService.virement(Compte source, Compte dest, BigDecimal montant, String lieu);

// Trouver un compte
Compte compte = compteService.findByNumero(String numero);
```

### TransactionService

```java
// Ajouter une transaction
transactionService.addTransaction(BigDecimal montant, String lieu, 
                                  TypeTransaction type, String idCompte);

// Historique trié
List<Transaction> transactions = transactionService.trieeByCompte(Compte compte);

// Statistiques
BigDecimal total = transactionService.calculerLaMoyenneOuTotal(
    null, compte, "total");
BigDecimal moyenne = transactionService.calculerLaMoyenneOuTotal(
    null, compte, "moyenne");
```

### RapportService

```java
// Top 5 clients
List<Compte> top5 = rapportService.top5Client();

// Rapport mensuel
rapportService.monthlyReport(int annee, int mois);

// Comptes inactifs
List<Compte> inactifs = rapportService.identifyInactiveAccounts(int jours);

// Transactions suspectes
List<Transaction> suspectes = rapportService.identifySuspiciousTransactions();
```

---

## 💡 Exemples d'Utilisation

### Exemple 1 : Créer un Client et un Compte

```java
// Créer un client
clientService.addClient("Ahmed Benali", "ahmed.benali@email.com");

// Récupérer le client
Client client = clientService.findBynome("Ahmed Benali").get();

// Créer un compte courant avec 1000€ et 500€ de découvert
compteService.addCompte(
    new BigDecimal("1000.00"),  // Solde initial
    500.0,                       // Découvert autorisé
    0.0,                         // Taux intérêt (non applicable)
    client,                      // Client
    TypeCompte.COURANT          // Type de compte
);
```

### Exemple 2 : Effectuer un Versement

```java
// Trouver le compte
Compte compte = compteService.findByNumero("CP050624");

// Effectuer un versement de 500€
compteService.versement(
    compte, 
    new BigDecimal("500.00"), 
    "Agence Centrale"
);

// Enregistrer la transaction
transactionService.addTransaction(
    new BigDecimal("500.00"),
    "Agence Centrale",
    TypeTransaction.VERSEMENT,
    compte.getId()
);
```

### Exemple 3 : Effectuer un Virement

```java
// Trouver les comptes
Compte source = compteService.findByNumero("CP050624");
Compte destination = compteService.findByNumero("CP159a7f");

// Effectuer le virement de 300€
compteService.virement(
    source,
    destination,
    new BigDecimal("300.00"),
    "Virement en ligne"
);

// Enregistrer la transaction
transactionService.addTransaction(
    new BigDecimal("300.00"),
    "Virement en ligne",
    TypeTransaction.VIREMENT,
    source.getId()
);
```

### Exemple 4 : Générer un Rapport Mensuel

```java
// Rapport pour octobre 2025
rapportService.monthlyReport(2025, 10);

// Sortie :
// === Rapport Mensuel 2025-10 ===
// Nombre de transactions par type:
// VERSEMENT: 45 transactions
// RETRAIT: 32 transactions
// VIREMENT: 18 transactions
//
// Volume total par type:
// VERSEMENT: 125000.00 €
// RETRAIT: 89000.00 €
// VIREMENT: 45000.00 €
```

---

## 🧪 Tests

### Tests Manuels

L'application dispose d'un menu interactif complet pour tester toutes les fonctionnalités.

### Scénarios de Test Recommandés

1. **Test de Création**
   - Créer un client
   - Créer un compte courant et un compte épargne
   - Vérifier les données

2. **Test des Opérations**
   - Effectuer un versement
   - Effectuer un retrait (avec et sans découvert)
   - Effectuer un virement
   - Vérifier les soldes

3. **Test des Validations**
   - Tenter un retrait avec solde insuffisant
   - Tenter un virement avec montant négatif
   - Vérifier les messages d'erreur

4. **Test des Rapports**
   - Générer le top 5 des clients
   - Consulter l'historique des transactions
   - Détecter les comptes inactifs

---

## 🛡️ Sécurité et Bonnes Pratiques

### Validations Implémentées

- ✅ Validation des emails (format RFC 5322)
- ✅ Validation des noms (caractères alphabétiques uniquement)
- ✅ Validation des montants (positifs uniquement)
- ✅ Vérification du découvert autorisé
- ✅ Prévention des injections SQL (PreparedStatement)

### Gestion des Erreurs

- ✅ Try-catch pour toutes les opérations DB
- ✅ Messages d'erreur explicites
- ✅ Rollback automatique en cas d'erreur

### Performance

- ✅ Index sur les clés étrangères
- ✅ Index sur les colonnes fréquemment recherchées
- ✅ Utilisation de PreparedStatement (cache des requêtes)

---


## 📝 Licence

Ce projet est développé à des fins éducatives dans le cadre du programme YouCode.

---

## 👥 Contributeurs

- **Anouar El barry** - Développeur Principal - [GitHub](https://github.com/anwar-elbarry)

---

## 📞 Contact

Pour toute question ou suggestion :

- **Email** : elbarry.anouar.contact@gmail.com
- **GitHub** : [github.com/anwar-elbarry](https://github.com/anwar-elbarry)

---

## 🙏 Remerciements

- **YouCode** pour la formation et l'opportunité d'apprentissage
- **Formateur Nafia Akdi** pour son accompagnement, ses conseils précieux et son aide tout au long du projet
- **Java Community** pour les ressources et la documentation
---

<div align="center">
  <p>Fait avec ❤️ par anouar El barry</p>
</div>