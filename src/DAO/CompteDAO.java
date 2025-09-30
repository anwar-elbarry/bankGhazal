package DAO;

import Entity.Client;
import Entity.Compte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class CompteDAO {
    private Connection connection;

    public CompteDAO(Connection connection){
        setConnection(connection);
    }


    public void addCompte(Compte compte)throws SQLException {
        String sql = "INSERT INTO compte (id,numero,solde,id_client) VALUES (? ,?, ?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,compte.getId());
            ps.setString(2,compte.getNumero());
            ps.setBigDecimal(3,compte.getSolde());
            ps.setObject(4,compte.getIdClient());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to add compte");
            }
        }
    }

    public void modifyCompte(Compte compte)throws SQLException{
        String sql = "UPDATE compte SET solde = ?  WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBigDecimal(1,compte.getSolde());
            ps.setObject(2,compte.getId());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to update compte");
            }
        }
    }


    public void removeCompte(Compte compte)throws SQLException{
        String sql = "DELETE FROM compte WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,compte.getId());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to remove compte");
            }
        }
    }

    public Optional<Compte> findByClient(Client client)throws SQLException{
        String sql = "SELECT * FROM compte WHERE id_client = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,client.id());
           try(ResultSet result = ps.executeQuery()){
               if (result.next()){
                 Compte compte =  new Compte(
                           result.getObject("id",UUID.class),
                           result.getString("numero"),
                           result.getBigDecimal("solde"),
                           result.getObject("id_client",UUID.class)
                   );
                 return Optional.of(compte);
               }
           }
        }
        return Optional.empty();
    }

    public void setConnection(Connection connection){
     this.connection = connection;
    }

    public Connection getConnection(){
        return this.connection;
    }
}
