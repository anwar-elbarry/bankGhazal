package Service;

import DAO.CompteDAO;
import DAO.TransactionDAO;
import Entity.Compte;
import Entity.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class RapportService {
    private CompteDAO compteDAO;
    public RapportService(CompteDAO compteDAO){
        this.compteDAO = compteDAO;
    }
    public List<Compte> top5Client()throws SQLException {
        try {
            return compteDAO.findAll().stream().sorted(Comparator.comparing(Compte::getSolde).reversed()).limit(5).toList();
        }catch (SQLException e){
            throw new SQLException("Failed to find top 5 clients: " + e.getMessage());
        }
    }
}
