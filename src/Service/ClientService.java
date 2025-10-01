package Service;

import DAO.ClienDAO;
import Entity.Client;
import Utilitaire.Validator;

import java.sql.SQLException;
import java.util.*;

public class ClientService {
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
        try {
            clientDAO.removeClient(client);
        }catch (SQLException e){
            throw new SQLException("Failed to remove client :"+e.getMessage());
        }

    }

    public List<Client> findAll()throws SQLException{
        try {
           return clientDAO.findAll();
        }catch (SQLException e){
            throw new SQLException("Failed to find all clients :"+e.getMessage());
        }
    }

    public Optional<Client> findClientById(String id)throws SQLException{
        try {
            return clientDAO.findClientById(id);
        }catch (SQLException e){
            throw new SQLException("Failed to find client by id :"+e.getMessage());
        }
    }
}
