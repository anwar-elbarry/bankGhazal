package DAO;

import Entity.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class ClienDAO {
    private Connection connection;
    public ClienDAO(Connection connection){
        setConnection(connection);
    }

    public void addClient(Client client)throws SQLException {
        String sql = "INSERT INTO client (id,nom,email) VALUES (? ,?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,client.id());
            ps.setString(2,client.nom());
            ps.setString(3,client.email());
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

    public Optional<Client> findClientById(UUID id) throws SQLException{
        String sql = "SELECT * FROM client WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setObject(1,id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Client client = new Client(
                            rs.getObject("id",UUID.class),
                            rs.getString("nom"),
                            rs.getString("email")
                    );
                    return Optional.of(client);
                }
            }
        }
        return Optional.empty();
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

}
