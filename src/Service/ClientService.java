package Service;

import DAO.ClienDAO;
import Entity.Client;
import Utilitaire.Validator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientService {
    private Map<UUID,Client>clients = new HashMap<>();
    private ClienDAO clientDAO;
    public ClientService(ClienDAO clientDAO){
        this.clientDAO = clientDAO;
    }

    public void addClient(String nom, String email)throws SQLException {

        if (!Validator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email");
        } else if (!Validator.isValidName(nom)) {
            throw new IllegalArgumentException("Invalid name");
        }
        try {
            Client client = new Client(UUID.randomUUID(),nom,email);
            clientDAO.addClient(client);
            clients.put(client.id(),client);
        }catch (SQLException e){
            throw new SQLException("Failed to add client :"+e.getMessage());
        }
    }

    public void modifyClient(Client client)throws SQLException {
        if (!Validator.isValidEmail(client.email())) {
            throw new IllegalArgumentException("Invalid email");
        } else if (!Validator.isValidName(client.nom())) {
            throw new IllegalArgumentException("Invalid name");
        }
        clientDAO.modifyClient(client);
    }

    public void removeClient(Client client)throws SQLException {
        clientDAO.removeClient(client);
    }
}
