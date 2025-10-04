package Service;

import DAO.CompteDAO;
import DAO.TransactionDAO;
import Entity.Compte;
import Entity.Enum.TypeTransaction;
import Entity.Transaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RapportService {
    private CompteDAO compteDAO;
    private TransactionDAO transactionDAO;

    public RapportService(CompteDAO compteDAO, TransactionDAO transactionDAO){
        this.compteDAO = compteDAO;
        this.transactionDAO = transactionDAO;
    }

    public List<Compte> top5Client()throws SQLException {
        try {
            return compteDAO.findAll().stream().sorted(Comparator.comparing(Compte::getSolde).reversed()).limit(5).toList();
        }catch (SQLException e){
            throw new SQLException("Failed to find top 5 clients: " + e.getMessage());
        }
    }

    public void monthlyReport(int year, int month) throws SQLException {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999);

            Map<TypeTransaction, Long> transactionCountByType = transactionDAO.findAll().stream()
                .filter(t -> {
                    LocalDateTime transactionDate = t.date();
                    return transactionDate.isAfter(startOfMonth.minusSeconds(1)) &&
                           transactionDate.isBefore(endOfMonth.plusSeconds(1));
                })
                .collect(Collectors.groupingBy(
                    Transaction::type,
                    Collectors.counting()
                ));

            Map<TypeTransaction, BigDecimal> totalVolumeByType = transactionDAO.findAll().stream()
                .filter(t -> {
                    LocalDateTime transactionDate = t.date();
                    return transactionDate.isAfter(startOfMonth.minusSeconds(1)) &&
                           transactionDate.isBefore(endOfMonth.plusSeconds(1));
                })
                .collect(Collectors.groupingBy(
                    Transaction::type,
                    Collectors.mapping(Transaction::montant, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

            System.out.println("=== Rapport Mensuel " + yearMonth + " ===");
            System.out.println("Nombre de transactions par type:");
            transactionCountByType.forEach((type, count) ->
                System.out.println(type + ": " + count + " transactions")
            );

            System.out.println("\nVolume total par type:");
            totalVolumeByType.forEach((type, volume) ->
                System.out.println(type + ": " + volume + " €")
            );

        } catch (SQLException e) {
            throw new SQLException("Failed to generate monthly report: " + e.getMessage());
        }
    }

    public List<Compte> identifyInactiveAccounts(int daysInactive) throws SQLException {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysInactive);
            List<Compte> allAccounts = compteDAO.findAll();
            List<Transaction> allTransactions = transactionDAO.findAll();

            Map<String, LocalDateTime> lastTransactionByAccount = allTransactions.stream()
                .collect(Collectors.groupingBy(
                    Transaction::idCompte,
                    Collectors.mapping(Transaction::date, Collectors.maxBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));

            return allAccounts.stream()
                .filter(compte -> {
                    LocalDateTime lastTransaction = lastTransactionByAccount.get(compte.getId());
                    return lastTransaction == null || lastTransaction.isBefore(cutoffDate);
                })
                .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new SQLException("Failed to identify inactive accounts: " + e.getMessage());
        }
    }

    public List<Transaction> identifySuspiciousTransactions() throws SQLException {
        try {
            List<Transaction> allTransactions = transactionDAO.findAll();

            BigDecimal highAmountThreshold = new BigDecimal("10000");
            int rapidTransactionCount = 5;
            long rapidTransactionMinutes = 60;

            List<Transaction> suspicious = allTransactions.stream()
                .filter(t -> t.montant().compareTo(highAmountThreshold) > 0)
                .collect(Collectors.toList());

            allTransactions.sort(Comparator.comparing(Transaction::date));
            for (int i = 0; i < allTransactions.size() - rapidTransactionCount + 1; i++) {
                List<Transaction> window = allTransactions.subList(i, i + rapidTransactionCount);
                String accountId = window.get(0).idCompte();

                boolean sameAccount = window.stream().allMatch(t -> t.idCompte().equals(accountId));
                if (sameAccount) {
                    LocalDateTime earliest = window.get(0).date();
                    LocalDateTime latest = window.get(rapidTransactionCount - 1).date();
                    long minutesBetween = java.time.temporal.ChronoUnit.MINUTES.between(earliest, latest);

                    if (minutesBetween <= rapidTransactionMinutes) {
                        suspicious.addAll(window);
                    }
                }
            }

            return suspicious.stream().distinct().collect(Collectors.toList());

        } catch (SQLException e) {
            throw new SQLException("Failed to identify suspicious transactions: " + e.getMessage());
        }
    }
}
