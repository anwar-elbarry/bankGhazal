package DAO;

import Entity.Client;
import Entity.Compte;
import Entity.Transaction;
import Entity.Enum.TypeTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAO {
    private Connection connection;
    public TransactionDAO(Connection connection){
        setConnection(connection);
    }

    public void addTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transaction (id,montant,date,lieu,type,idcompte) VALUES (? ,?, ?, ?, ? ,?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,transaction.id());
            ps.setBigDecimal(2,transaction.montant());
            ps.setTimestamp(3, Timestamp.valueOf(transaction.date()));
            ps.setString(4,transaction.lieu());
            ps.setString(5,transaction.type().toString());
            ps.setString(6,transaction.idCompte());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to add transaction");
            }
        }
    }

    public void removeTransaction(Transaction transaction) throws SQLException {
        String sql = "DELETE FROM transaction WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,transaction.id());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to remove transaction");
            }
        }
    }

    public void modifyTransaction(Transaction transaction) throws SQLException {
        String sql = "UPDATE transaction SET montant = ?, date = ?, lieu = ?, type = ?, idcompte = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBigDecimal(1,transaction.montant());
            ps.setString(2,transaction.lieu());
            ps.setObject(3,transaction.type());
            ps.setString(4,transaction.idCompte());
            ps.setString(5,transaction.id());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to update transaction");
            }
        }
    }

    public List<Transaction> findTransactionByCompte(Compte compte) throws SQLException {
        String sql = "SELECT * FROM transaction WHERE idcompte = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,compte.getId());
            try(ResultSet result = ps.executeQuery()){
                List<Transaction> transactions = new ArrayList<>();
                while (result.next()){
                    Transaction transaction = new Transaction(
                            result.getString("id"),
                            result.getBigDecimal("montant"),
                            result.getTimestamp("date").toLocalDateTime(),
                            result.getString("lieu"),
                            result.getObject("type", TypeTransaction.class),
                            result.getString("idcompte")
                    );
                    transactions.add(transaction);
                }
                return transactions;
            }
        }
    }
   public List<Transaction> findTransactionByClient(Client client) throws SQLException {
        String sql = """
                SELECT
                tr.*
                FROM transaction tr 
                inner join compte cp on tr.idcompte = cp.id
                inner join client cl on cp.idclient = cl.id
                where cl.id = ?
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,client.id());
            try(ResultSet result = ps.executeQuery()){
                List<Transaction> transactions = new ArrayList<>();
                while (result.next()){
                    Transaction transaction = new Transaction(
                            result.getString("id"),
                            result.getBigDecimal("montant"),
                            result.getTimestamp("date").toLocalDateTime(),
                            result.getString("lieu"),
                            result.getObject("type", TypeTransaction.class),
                            result.getString("idcompte")
                    );
                    transactions.add(transaction);
                }
                return transactions;
            }
        }
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
                            result.getString("id"),
                            result.getBigDecimal("montant"),
                            result.getTimestamp("date").toLocalDateTime(),
                            result.getString("lieu"),
                            result.getObject("type", TypeTransaction.class),
                            result.getString("idcompte"));
                    transactions.add(transaction);
                }
                return Optional.of(transactions);
            }
        }
    }

    public List<Transaction> findAll()  throws SQLException{
        String sql = "SELECT * FROM transaction";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            try(ResultSet result = ps.executeQuery()){
                List<Transaction> transactions = new ArrayList<>();
                while (result.next()){
                    Transaction transaction = new Transaction(
                            result.getString("id"),
                            result.getBigDecimal("montant"),
                            result.getTimestamp("date").toLocalDateTime(),
                            result.getString("lieu"),
                            result.getObject("type", TypeTransaction.class),
                            result.getString("idcompte")
                    );
                    transactions.add(transaction);
                }
                return transactions;
            }

        }catch (SQLException e){
            throw new SQLException("Failed to find all transactions: " + e.getMessage());
        }
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
