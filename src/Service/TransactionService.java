package Service;

import DAO.TransactionDAO;
import Entity.Transaction;
import Entity.TypeTransaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionService {
    private TransactionDAO transactionDAO;
    public TransactionService(TransactionDAO transactionDAO){
        this.transactionDAO = transactionDAO;
    }
    public void addTransaction(BigDecimal montant,String lieu, TypeTransaction type, UUID idCompte) throws SQLException {
        try{
                if (montant.compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalArgumentException("montant must be greater than zero");
                } else if (type == null) {
                    throw new IllegalArgumentException("le type est obligatoire");
                }
                UUID id = UUID.randomUUID();
                LocalDateTime date = LocalDateTime.now();
                Transaction transaction = new Transaction(id,montant,date,lieu,type,idCompte);
                transactionDAO.addTransaction(transaction);
        }catch (SQLException e){
            throw new SQLException("Failed to add transaction: " + e.getMessage());
        }
    }

}
