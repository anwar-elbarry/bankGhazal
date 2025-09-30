package DAO;

import Entity.Compte;
import Entity.Transaction;
import Entity.TypeTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionDAO {
    private Connection connection;
    public TransactionDAO(Connection connection){
        setConnection(connection);
    }

    public void addTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transaction (id,montant,date,lieu,type,id_compte) VALUES (? ,?, ?, ?, ? ,?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,transaction.id());
            ps.setBigDecimal(2,transaction.montant());
            ps.setString(3,transaction.lieu());
            ps.setObject(4,transaction.type());
            ps.setObject(5,transaction.idCompte());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to add transaction");
            }
        }
    }

    public void removeTransaction(Transaction transaction) throws SQLException {
        String sql = "DELETE FROM transaction WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,transaction.id());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to remove transaction");
            }
        }
    }

    public void modifyTransaction(Transaction transaction) throws SQLException {
        String sql = "UPDATE transaction SET montant = ?, date = ?, lieu = ?, type = ?, id_compte = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBigDecimal(1,transaction.montant());
            ps.setString(2,transaction.lieu());
            ps.setObject(3,transaction.type());
            ps.setObject(4,transaction.idCompte());
            ps.setObject(5,transaction.id());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to update transaction");
            }
        }
    }

    public Optional<Transaction> findTransactionByCompte(Compte compte) throws SQLException {
        String sql = "SELECT * FROM transaction WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,compte.getId());
            try(ResultSet result = ps.executeQuery()){
                if(result.next()){
                    Transaction transaction = new Transaction(
                            result.getObject("id",UUID.class),
                            result.getBigDecimal("montant"),
                            result.getTimestamp("date").toLocalDateTime(),
                            result.getString("lieu"),
                            result.getObject("type", TypeTransaction.class),
                            result.getObject("id_compte",UUID.class)
                    );
                    return Optional.of(transaction);
                }
            }
        }
        return Optional.empty();
    }

    public Optional <List<Transaction>> recherchGlobale(String recherche)throws SQLException{
        String sql = """
                SELECT *
                FROM transaction
                WHERE lower(lieu) LIKE ?
                   OR lower(type) LIKE ?
                   OR lower(montant) LIKE ?
                   OR lower(date) LIKE ?
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            String pattern = "%" + recherche.toLowerCase() + "%";
            ps.setString(1,pattern);
            ps.setString(2,pattern);
            ps.setString(3,pattern);
            ps.setString(4,pattern);
            try(ResultSet result = ps.executeQuery()){
                List<Transaction> transactions = new ArrayList<>();
                while(result.next()){
                    Transaction transaction = new Transaction(
                            result.getObject("id",UUID.class),
                            result.getBigDecimal("montant"),
                            result.getTimestamp("date").toLocalDateTime(),
                            result.getString("lieu"),
                            result.getObject("type", TypeTransaction.class),
                            result.getObject("id_compte",UUID.class));
                    transactions.add(transaction);
                }
                return Optional.of(transactions);
            }
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
