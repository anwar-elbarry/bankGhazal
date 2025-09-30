package DAO;

import Entity.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClienDAO {
    private Connection connection;
    public ClienDAO(Connection connection){
        setConnection(connection);
    }

    public void addClient(Client client)throws SQLException {
        String sql = "INSERT INTO client (nom,email) VALUES (?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,client.nom());
            ps.setString(2,client.email());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to add client");
            }
        }
    }
    public void modifyClient(Client client)throws SQLException {
        String sql = "UPDATE client SET nom = ?, email = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,client.nom());
            ps.setString(2,client.email());
            ps.setObject(3,client.id());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to update client");
            }
        }
    }

    public void removeClient(Client client)throws SQLException {
        String sql = "DELETE FROM client WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,client.id());
            int result = ps.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to remove client");
            }
        }
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

}
