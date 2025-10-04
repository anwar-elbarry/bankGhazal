package DAO;

import Entity.Client;
import Entity.Compte;
import Entity.Enum.TypeCompte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CompteDAO {
    private Connection connection;

    public CompteDAO(Connection connection){
        setConnection(connection);
    }


    public void addCompte(Compte compte,double tauxInteret,double decouvertAutorise)throws SQLException {
        String sql = "INSERT INTO compte (id,numero,solde,idclient,typecompte,decouvertautorise,tauxinteret) VALUES (? ,?, ?, ?,?,?,?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,compte.getId());
            ps.setString(2,compte.getNumero());
            ps.setBigDecimal(3,compte.getSolde());
            ps.setString(4,compte.getIdClient());
            ps.setString(5,compte.getTypeCompte().toString());
            ps.setDouble(6,decouvertAutorise);
            ps.setDouble(7,tauxInteret);
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
            ps.setString(2,compte.getId());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to update compte");
            }
        }
    }


    public void removeCompte(Compte compte)throws SQLException{
        String sql = "DELETE FROM compte WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,compte.getId());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to remove compte");
            }
        }
    }

    public List<Compte> findAll()throws SQLException{
        String sql = "SELECT * FROM compte";
        List<Compte>ClientList = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
           try(ResultSet result = ps.executeQuery()){
               while(result.next()){
                 Compte compte =  new Compte(
                           result.getString("id"),
                           result.getString("numero"),
                           result.getBigDecimal("solde"),
                           result.getString("idclient"),
                           result.getString("typecompte").equals("COURANT") ? TypeCompte.COURANT : TypeCompte.EPARGNE
                   );
                 ClientList.add(compte);
               }
               return ClientList;
           }
        }
    }
    public Optional<List<Compte>> findByClient(Client client)throws SQLException{
        String sql = "SELECT * FROM compte WHERE id_client = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,client.id());
           try(ResultSet result = ps.executeQuery()){
               List<Compte>clientsList = new ArrayList<>();
               while (result.next()){
                 Compte compte =  new Compte(
                           result.getString("id"),
                           result.getString("numero"),
                           result.getBigDecimal("solde"),
                           result.getString("idclient"),
                           result.getString("typecompte").equals("COURANT") ? TypeCompte.COURANT : TypeCompte.EPARGNE
                   );
                 clientsList.add(compte);
               }
               return Optional.of(clientsList);
           }
        }

    }

    public Optional<Compte> findById(String id)throws SQLException{
        String sql = "SELECT * FROM compte WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            UUID uuid = UUID.fromString(id);
            ps.setObject(1,uuid);
            try(ResultSet result = ps.executeQuery()){
                if (result.next()){
                    Compte compte =  new Compte(
                            result.getString("id"),
                            result.getString("numero"),
                            result.getBigDecimal("solde"),
                            result.getString("idclient"),
                            result.getString("typecompte").equals("COURANT") ? TypeCompte.COURANT : TypeCompte.EPARGNE
                    );
                  return Optional.of(compte);
                }
                return Optional.empty();
            }
        }

    }public Optional<Compte> findByNumero(String numero)throws SQLException{
        String sql = "SELECT * FROM compte WHERE numero = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,numero);
            try(ResultSet result = ps.executeQuery()){
                if (result.next()){
                    Compte compte =  new Compte(
                            result.getString("id"),
                            result.getString("numero"),
                            result.getBigDecimal("solde"),
                            result.getString("idclient"),
                            result.getString("typecompte").equals("COURANT") ? TypeCompte.COURANT : TypeCompte.EPARGNE
                    );
                  return Optional.of(compte);
                }
                return Optional.empty();
            }
        }

    }

    public void setConnection(Connection connection){
     this.connection = connection;
    }

    public Connection getConnection(){
        return this.connection;
    }
}
