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

}
