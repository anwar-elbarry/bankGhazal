package Service;

import DAO.TransactionDAO;
import Entity.Client;
import Entity.Compte;
import Entity.Transaction;
import Entity.Enum.TypeTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionService {
    private TransactionDAO transactionDAO;
    public TransactionService(TransactionDAO transactionDAO){
        this.transactionDAO = transactionDAO;
    }
    public void addTransaction(BigDecimal montant,String lieu, TypeTransaction type, String idCompte) throws SQLException {
        try{
                if (montant.compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalArgumentException("montant must be greater than zero");
                } else if (type == null) {
                    throw new IllegalArgumentException("le type est obligatoire");
                }
                String id = UUID.randomUUID().toString();
                LocalDateTime date = LocalDateTime.now();
                Transaction transaction = new Transaction(id,montant,date,lieu,type,idCompte);
                transactionDAO.addTransaction(transaction);
        }catch (SQLException e){
            throw new SQLException("Failed to add transaction: " + e.getMessage());
        }
    }

    public List<Transaction> filter(TransactionFilter filter) throws SQLException {
        try {
            List<Transaction> transactions = transactionDAO.findAll();
            return transactions.stream()
                    .filter(e -> filter.getType() == null || e.type().equals(filter.getType()))
                    .filter(e -> filter.getMinMontant() == null || e.montant().compareTo(BigDecimal.valueOf(filter.getMinMontant())) >= 0)
                    .filter(e -> filter.getMaxMontant() == null || e.montant().compareTo(BigDecimal.valueOf(filter.getMaxMontant())) <= 0)
                    .filter(e -> filter.getStartDate() == null || e.date().isAfter(filter.getStartDate()) || e.date().isEqual(filter.getStartDate()))
                    .filter(e -> filter.getEndDate() == null || e.date().isBefore(filter.getEndDate()) || e.date().isEqual(filter.getEndDate()))
                    .filter(e -> filter.getLieu() == null || e.lieu().toLowerCase().contains(filter.getLieu().toLowerCase()))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new SQLException("Failed to filter transactions: " + e.getMessage());
        }
    }

    public List<Transaction> trieeByCompte(Compte compte) throws SQLException {
        List<Transaction> transactions = transactionDAO.findTransactionByCompte(compte);
        transactions.sort(Comparator.comparing(Transaction::date));
        return transactions;
    }
    public List<Transaction> trieeByClient(Client client) throws SQLException {
        List<Transaction> transactions = transactionDAO.findAll().stream().filter(e -> e.idCompte().equals(client.id())).collect(Collectors.toList());
        transactions.sort(Comparator.comparing(Transaction::date));
        return transactions;
    }

    public BigDecimal calculerLaMoyenneOuTotal(Client client, Compte compte, String type) throws SQLException {
        try {
            // 1. Fetch transactions (once)
            List<Transaction> transactions = (client != null)
                    ? transactionDAO.findTransactionByClient(client)
                    : transactionDAO.findTransactionByCompte(compte);

            if (transactions.isEmpty()) {
                return BigDecimal.ZERO; // no transactions
            }

            // 2. Compute based on type
            switch (type.toLowerCase()) {
                case "total":
                    return transactions.stream()
                            .map(Transaction::montant)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                case "moyenne":
                    BigDecimal sum = transactions.stream()
                            .map(Transaction::montant)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return sum.divide(BigDecimal.valueOf(transactions.size()), RoundingMode.HALF_UP);

                default:
                    throw new IllegalArgumentException("Type must be 'total' or 'moyenne'");
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to calculate total or average", e);
        }
    }

    public List<Transaction> detecterTransactionsSuspectes(Client client) throws SQLException {
        List<Transaction> transactions = transactionDAO.findTransactionByClient(client);
        if (transactions.isEmpty()) {
            return List.of();
        }

        BigDecimal highAmountThreshold = new BigDecimal("10000");
        int frequencyThreshold = 5; // per hour
        double unusualLocationThreshold = 0.1; // less than 10% of transactions

        Set<Transaction> highAmount = transactions.stream()
                .filter(t -> t.montant().compareTo(highAmountThreshold) > 0)
                .collect(Collectors.toSet());

        Map<String, Long> locationCount = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::lieu, Collectors.counting()));
        long totalTransactions = transactions.size();
        Set<String> commonLocations = locationCount.entrySet().stream()
                .filter(entry -> (double) entry.getValue() / totalTransactions > unusualLocationThreshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        Set<Transaction> unusualLocation = transactions.stream()
                .filter(t -> !commonLocations.contains(t.lieu()))
                .collect(Collectors.toSet());

        Set<Transaction> excessiveFrequency = new HashSet<>();
        for (int i = 0; i < transactions.size() - frequencyThreshold + 1; i++) {
            List<Transaction> window = transactions.subList(i, i + frequencyThreshold);
            LocalDateTime earliest = window.get(0).date();
            LocalDateTime latest = window.get(frequencyThreshold - 1).date();
            if (ChronoUnit.HOURS.between(earliest, latest) <= 1) {
                excessiveFrequency.addAll(window);
            }
        }

        Set<Transaction> suspicious = new HashSet<>();
        suspicious.addAll(highAmount);
        suspicious.addAll(unusualLocation);
        suspicious.addAll(excessiveFrequency);

        return new ArrayList<>(suspicious);
    }
}
