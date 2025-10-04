package Presentation;

import DAO.ClienDAO;
import DAO.CompteDAO;
import DAO.TransactionDAO;
import DAO.Util.DatabaseConnection;
import Entity.*;
import Entity.Enum.TypeCompte;
import Entity.Enum.TypeTransaction;
import Service.ClientService;
import Service.CompteService;
import Service.RapportService;
import Service.TransactionService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    static CompteService compteService;
    static ClientService clientService;
    static TransactionService transactionService;
    static RapportService rapportService;
    static ClienDAO clienDAO;
    static CompteDAO compteDAO;
    static TransactionDAO transactionDAO;
    static Scanner scanner = new Scanner(System.in);

    static {
        try {
            Connection connection = DatabaseConnection.getConnection();
            clienDAO = new ClienDAO(connection);
            compteDAO = new CompteDAO(connection);
            transactionDAO = new TransactionDAO(connection);

            clientService = new ClientService(clienDAO);
            compteService = new CompteService(compteDAO);
            transactionService = new TransactionService(transactionDAO);
            rapportService = new RapportService(compteDAO, transactionDAO);
        } catch (SQLException e) {
            throw new RuntimeException("Initialization failed", e);
        }
    }

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            afficherMenuPrincipal();
            int choix = lireEntier("Votre choix: ");

            try {
                switch (choix) {
                    case 1 -> gererClients();
                    case 2 -> gererTransactions();
                    case 3 -> consulterHistorique();
                    case 4 -> lancerAnalyses();
                    case 5 -> afficherAlertes();
                    case 0 -> {
                        System.out.println("\n👋 Au revoir!");
                        running = false;
                    }
                    default -> System.out.println("❌ Choix invalide!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🏦  SYSTÈME DE GESTION BANCAIRE - BANK GHAZAL");
        System.out.println("=".repeat(60));
        System.out.println("1. 👤 Gestion des Clients et Comptes");
        System.out.println("2. 💰 Gestion des Transactions");
        System.out.println("3. 📜 Consulter l'Historique des Transactions");
        System.out.println("4. 📊 Analyses et Rapports");
        System.out.println("5. 🔔 Alertes et Notifications");
        System.out.println("0. 🚪 Quitter");
        System.out.println("=".repeat(60));
    }

    // ==================== GESTION DES CLIENTS ====================
    private static void gererClients() throws SQLException {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("👤 GESTION DES CLIENTS ET COMPTES");
        System.out.println("─".repeat(60));
        System.out.println("1. Créer un nouveau client");
        System.out.println("2. Créer un compte pour un client");
        System.out.println("3. Afficher tous les clients");
        System.out.println("4. Afficher les comptes d'un client");
        System.out.println("0. Retour");
        System.out.println("─".repeat(60));

        int choix = lireEntier("Votre choix: ");

        switch (choix) {
            case 1 -> creerClient();
            case 2 -> creerCompte();
            case 3 -> afficherTousLesClients();
            case 4 -> afficherComptesClient();
            case 0 -> {}
            default -> System.out.println("❌ Choix invalide!");
        }
    }

    private static void creerClient() throws SQLException {
        System.out.println("\n📝 CRÉATION D'UN NOUVEAU CLIENT");
        System.out.print("Nom: ");
        String nom = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        try {
            clientService.addClient(nom, email);
            System.out.println("✅ Client créé avec succès!");
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void creerCompte() throws SQLException {
        System.out.println("\n💳 CRÉATION D'UN NOUVEAU COMPTE");
        System.out.print("Nom du client: ");
        String nomClient = scanner.nextLine();

        Client client = clientService.findBynome(nomClient).orElse(null);
        if (client == null) {
            System.out.println("❌ Client non trouvé!");
            return;
        }

        System.out.println("Type de compte:");
        System.out.println("1. Compte Courant");
        System.out.println("2. Compte Épargne");
        int typeChoix = lireEntier("Votre choix: ");

        System.out.print("Solde initial: ");
        BigDecimal solde = new BigDecimal(scanner.nextLine());

        double decouvert = 0;
        double tauxInteret = 0;

        if (typeChoix == 1) {
            System.out.print("Découvert autorisé: ");
            decouvert = Double.parseDouble(scanner.nextLine());
            compteService.addCompte(solde, decouvert, 0, client, TypeCompte.COURANT);
        } else {
            System.out.print("Taux d'intérêt (%): ");
            tauxInteret = Double.parseDouble(scanner.nextLine());
            compteService.addCompte(solde, 0, tauxInteret, client, TypeCompte.EPARGNE);
        }

        System.out.println("✅ Compte créé avec succès!");
    }

    private static void afficherTousLesClients() throws SQLException {
        System.out.println("\n📋 LISTE DES CLIENTS");
        List<Client> clients = clientService.findAll();
        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé.");
            return;
        }

        System.out.println("─".repeat(80));
        System.out.printf("%-40s %-20s %-20s%n", "ID", "Nom", "Email");
        System.out.println("─".repeat(80));
        for (Client client : clients) {
            System.out.printf("%-40s %-20s %-20s%n",
                    client.id(), client.nom(), client.email());
        }
        System.out.println("─".repeat(80));
    }

    private static void afficherComptesClient() throws SQLException {
        System.out.print("\nNom du client: ");
        String nomClient = scanner.nextLine();

        Client client = clientService.findBynome(nomClient).orElse(null);
        if (client == null) {
            System.out.println("❌ Client non trouvé!");
            return;
        }

        List<Compte> comptes = compteService.findByClient(client);
        if (comptes.isEmpty()) {
            System.out.println("Aucun compte trouvé pour ce client.");
            return;
        }

        System.out.println("\n💳 COMPTES DE " + client.nom());
        System.out.println("─".repeat(80));
        System.out.printf("%-15s %-15s %-15s %-15s%n", "Numéro", "Type", "Solde", "Info");
        System.out.println("─".repeat(80));
        for (Compte compte : comptes) {
            String info = "";
            if (compte instanceof CompteCourant) {
                info = "Découvert: " + ((CompteCourant) compte).getDecouvertAutorise();
            } else if (compte instanceof CompteEpargne) {
                info = "Taux: " + ((CompteEpargne) compte).getTauxInteret() + "%";
            }else {
                info = "Taux: " + "ma3arfinch"+"%";
            }
            System.out.printf("%-15s %-15s %-15s %-15s%n",
                    compte.getNumero(), compte.getTypeCompte(), compte.getSolde(), info);
        }
        System.out.println("─".repeat(80));
    }

    // ==================== GESTION DES TRANSACTIONS ====================
    private static void gererTransactions() throws SQLException {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("💰 GESTION DES TRANSACTIONS");
        System.out.println("─".repeat(60));
        System.out.println("1. 💵 Versement (Dépôt)");
        System.out.println("2. 💸 Retrait");
        System.out.println("3. 🔄 Virement");
        System.out.println("0. Retour");
        System.out.println("─".repeat(60));

        int choix = lireEntier("Votre choix: ");

        switch (choix) {
            case 1 -> effectuerVersement();
            case 2 -> effectuerRetrait();
            case 3 -> effectuerVirement();
            case 0 -> {}
            default -> System.out.println("❌ Choix invalide!");
        }
    }

    private static void effectuerVersement() throws SQLException {
        System.out.println("\n💵 VERSEMENT");
        System.out.print("Numéro de compte: ");
        String numeroCompte = scanner.nextLine();

        Compte compte = compteService.findByNumero(numeroCompte);
        if (compte == null) {
            System.out.println("❌ Compte non trouvé!");
            return;
        }

        System.out.print("Montant à verser: ");
        BigDecimal montant = new BigDecimal(scanner.nextLine());
        System.out.print("Lieu: ");
        String lieu = scanner.nextLine();

        try {
            compteService.versement(compte, montant, lieu);
            transactionService.addTransaction(montant, lieu, TypeTransaction.VERSEMENT, compte.getId());
            System.out.println("✅ Versement effectué avec succès!");
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void effectuerRetrait() throws SQLException {
        System.out.println("\n💸 RETRAIT");
        System.out.print("Numéro de compte: ");
        String numeroCompte = scanner.nextLine();

        Compte compte = compteService.findByNumero(numeroCompte);
        if (compte == null) {
            System.out.println("❌ Compte non trouvé!");
            return;
        }

        System.out.print("Montant à retirer: ");
        BigDecimal montant = new BigDecimal(scanner.nextLine());
        System.out.print("Lieu: ");
        String lieu = scanner.nextLine();

        try {
            compteService.retrait(compte, montant, lieu);
            transactionService.addTransaction(montant, lieu, TypeTransaction.RETRAIT, compte.getId());
            System.out.println("✅ Retrait effectué avec succès!");
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void effectuerVirement() throws SQLException {
        System.out.println("\n🔄 VIREMENT");
        System.out.print("Numéro de compte source: ");
        String numeroSource = scanner.nextLine();
        System.out.print("Numéro de compte destination: ");
        String numeroDestination = scanner.nextLine();

        Compte compteSource = compteService.findByNumero(numeroSource);
        Compte compteDestination = compteService.findByNumero(numeroDestination);

        if (compteSource == null || compteDestination == null) {
            System.out.println("❌ Un ou plusieurs comptes non trouvés!");
            return;
        }

        System.out.print("Montant à virer: ");
        BigDecimal montant = new BigDecimal(scanner.nextLine());
        System.out.print("Lieu: ");
        String lieu = scanner.nextLine();

        try {
            compteService.virement(compteSource, compteDestination, montant, lieu);
            transactionService.addTransaction(montant, lieu, TypeTransaction.VIREMENT, compteSource.getId());
            System.out.println("✅ Virement effectué avec succès!");
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    // ==================== HISTORIQUE DES TRANSACTIONS ====================
    private static void consulterHistorique() throws SQLException {
        System.out.println("\n📜 HISTORIQUE DES TRANSACTIONS");
        System.out.print("Numéro de compte: ");
        String numeroCompte = scanner.nextLine();

        Compte compte = compteService.findByNumero(numeroCompte);
        if (compte == null) {
            System.out.println("❌ Compte non trouvé!");
            return;
        }

        List<Transaction> transactions = transactionService.trieeByCompte(compte);
        if (transactions.isEmpty()) {
            System.out.println("Aucune transaction trouvée pour ce compte.");
            return;
        }

        System.out.println("\n📊 Transactions du compte " + numeroCompte);
        System.out.println("─".repeat(80));
        System.out.printf("%-25s %-15s %-15s %-20s",
                "Date", "Type", "Montant", "Lieu");
        System.out.println("─".repeat(80));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (Transaction transaction : transactions) {
            System.out.printf("%-25s %-15s %-15s %-20s",
                    transaction.date().format(formatter),
                    transaction.type(),
                    transaction.montant() + " €",
                    transaction.lieu());
        }
        System.out.println("─".repeat(80));

        BigDecimal total = transactionService.calculerLaMoyenneOuTotal(null, compte, "total");
        BigDecimal moyenne = transactionService.calculerLaMoyenneOuTotal(null, compte, "moyenne");
        System.out.println("\n📈 Statistiques:");
        System.out.println("Total des transactions: " + total + " €");
        System.out.println("Montant moyen: " + moyenne + " €");
    }

    // ==================== ANALYSES ET RAPPORTS ====================
    private static void lancerAnalyses() throws SQLException {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("📊 ANALYSES ET RAPPORTS");
        System.out.println("─".repeat(60));
        System.out.println("1. 🏆 Top 5 des clients (solde le plus élevé)");
        System.out.println("2. 📅 Rapport mensuel des transactions");
        System.out.println("3. 😴 Comptes inactifs");
        System.out.println("4. ⚠️  Transactions suspectes");
        System.out.println("0. Retour");
        System.out.println("─".repeat(60));

        int choix = lireEntier("Votre choix: ");

        switch (choix) {
            case 1 -> afficherTop5Clients();
            case 2 -> afficherRapportMensuel();
            case 3 -> afficherComptesInactifs();
            case 4 -> afficherTransactionsSuspectes();
            case 0 -> {}
            default -> System.out.println("❌ Choix invalide!");
        }
    }

    private static void afficherTop5Clients() throws SQLException {
        System.out.println("\n🏆 TOP 5 DES CLIENTS");
        List<Compte> topComptes = rapportService.top5Client();

        System.out.println("─".repeat(80));
        System.out.printf("%-15s %-15s %-20s %-15s%n", "Numéro", "Type", "Solde", "Client ID");
        System.out.println("─".repeat(80));

        for (int i = 0; i < topComptes.size(); i++) {
            Compte compte = topComptes.get(i);
            System.out.printf("%d. %-12s %-15s %-20s %-15s%n",
                    (i + 1),
                    compte.getNumero(),
                    compte.getTypeCompte(),
                    compte.getSolde() + " €",
                    compte.getIdClient().substring(0, 8) + "...");
        }
        System.out.println("─".repeat(80));
    }

    private static void afficherRapportMensuel() throws SQLException {
        System.out.println("\n📅 RAPPORT MENSUEL");
        System.out.print("Année: ");
        int annee = lireEntier("");
        System.out.print("Mois (1-12): ");
        int mois = lireEntier("");

        rapportService.monthlyReport(annee, mois);
    }

    private static void afficherComptesInactifs() throws SQLException {
        System.out.println("\n😴 COMPTES INACTIFS");
        System.out.print("Nombre de jours d'inactivité: ");
        int jours = lireEntier("");

        List<Compte> comptesInactifs = rapportService.identifyInactiveAccounts(jours);

        if (comptesInactifs.isEmpty()) {
            System.out.println("✅ Aucun compte inactif trouvé.");
            return;
        }

        System.out.println("\n⚠️  " + comptesInactifs.size() + " compte(s) inactif(s) trouvé(s):");
        System.out.println("─".repeat(80));
        System.out.printf("%-15s %-15s %-20s %-15s%n", "Numéro", "Type", "Solde", "Client ID");
        System.out.println("─".repeat(80));

        for (Compte compte : comptesInactifs) {
            System.out.printf("%-15s %-15s %-20s %-15s%n",
                    compte.getNumero(),
                    compte.getTypeCompte(),
                    compte.getSolde() + " €",
                    compte.getIdClient().substring(0, 8) + "...");
        }
        System.out.println("─".repeat(80));
    }

    private static void afficherTransactionsSuspectes() throws SQLException {
        System.out.println("\n⚠️  TRANSACTIONS SUSPECTES");
        List<Transaction> transactionsSuspectes = rapportService.identifySuspiciousTransactions();

        if (transactionsSuspectes.isEmpty()) {
            System.out.println("✅ Aucune transaction suspecte détectée.");
            return;
        }

        System.out.println("\n🚨 " + transactionsSuspectes.size() + " transaction(s) suspecte(s) détectée(s):");
        System.out.println("─".repeat(100));
        System.out.printf("%-25s %-15s %-15s %-20s%n", "Date", "Type", "Montant", "Lieu");
        System.out.println("─".repeat(100));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (Transaction transaction : transactionsSuspectes) {
            System.out.printf("%-25s %-15s %-15s %-20s%n",
                    transaction.date().format(formatter),
                    transaction.type(),
                    transaction.montant() + " €",
                    transaction.lieu());
        }
        System.out.println("─".repeat(100));
    }

    // ==================== ALERTES ====================
    private static void afficherAlertes() throws SQLException {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("🔔 ALERTES ET NOTIFICATIONS");
        System.out.println("─".repeat(60));

        // Alerte solde bas
        System.out.print("Seuil de solde bas: ");
        BigDecimal seuilSoldeBas = new BigDecimal(scanner.nextLine());

        List<Compte> comptes = compteService.findAll();
        List<Compte> comptesSoldeBas = comptes.stream()
                .filter(c -> c.getSolde().compareTo(seuilSoldeBas) < 0)
                .toList();

        if (!comptesSoldeBas.isEmpty()) {
            System.out.println("\n⚠️  ALERTE: " + comptesSoldeBas.size() + " compte(s) avec solde bas:");
            System.out.println("─".repeat(80));
            for (Compte compte : comptesSoldeBas) {
                System.out.printf("🔴 Compte %s - Solde: %s €%n",
                        compte.getNumero(), compte.getSolde());
            }
            System.out.println("─".repeat(80));
        } else {
            System.out.println("✅ Aucun compte avec solde bas.");
        }

        // Alerte inactivité
        System.out.print("\nNombre de jours d'inactivité pour alerte: ");
        int joursInactivite = lireEntier("");

        List<Compte> comptesInactifs = rapportService.identifyInactiveAccounts(joursInactivite);

        if (!comptesInactifs.isEmpty()) {
            System.out.println("\n⚠️  ALERTE: " + comptesInactifs.size() + " compte(s) inactif(s):");
            System.out.println("─".repeat(80));
            for (Compte compte : comptesInactifs) {
                System.out.printf("🟡 Compte %s - Inactif depuis plus de %d jours%n",
                        compte.getNumero(), joursInactivite);
            }
            System.out.println("─".repeat(80));
        } else {
            System.out.println("✅ Aucun compte inactif.");
        }
    }

    // ==================== UTILITAIRES ====================
    private static int lireEntier(String message) {
        System.out.print(message);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("❌ Veuillez entrer un nombre valide: ");
        }
        int valeur = scanner.nextInt();
        scanner.nextLine(); // Consommer le retour à la ligne
        return valeur;
    }
}