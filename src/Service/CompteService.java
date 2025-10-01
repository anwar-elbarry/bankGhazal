package Service;

import DAO.CompteDAO;
import Entity.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class CompteService {
    private CompteDAO compteDAO;
    public CompteService(CompteDAO compteDAO){
        setCompteDAO(compteDAO);
    }

    public void addCompte(BigDecimal solde, double decouvertAutorise, double tauxInteret, Client client, TypeCompte typeCompte) throws SQLException {
           if (solde.compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException("Solde must be greater than or equal to zero");
           }
           try{

           if (decouvertAutorise < 0){
                throw new IllegalArgumentException("Decouvert autorise must be greater than or equal to zero");
           }
           if (tauxInteret < 0){
                throw new IllegalArgumentException("Taux interet must be greater than or equal to zero");
           }
               UUID id = UUID.randomUUID();
               String numero = "CP" + id.toString().substring(0, 6);
               Compte compte;
               if(typeCompte == TypeCompte.COURANT){
                   compte = new CompteCourant(id,numero,solde,client.id(),decouvertAutorise);
               }else {
                   compte = new CompteEpargne(id,numero,solde,client.id(),tauxInteret);
               }
               compteDAO.addCompte(compte,decouvertAutorise,tauxInteret);
               System.out.println("Compte added successfully");
           }catch (SQLException e){
               throw new SQLException("Failed to add compte: " + e.getMessage());
           }
    }

    public void modifyCompte(Compte compte) throws SQLException {
        try{
            if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException("Solde must be greater than or equal to zero");
            }
            compteDAO.modifyCompte(compte);
        }catch (SQLException e){
            throw new SQLException("Failed to modify compte: " + e.getMessage());
        }
    }

    public void removeCompte(Compte compte) throws SQLException {
        try{
            compteDAO.removeCompte(compte);
        }catch (SQLException e){
            throw new SQLException("Failed to remove compte: " + e.getMessage());
        }
    }

    public List<Compte> findAll() throws SQLException {
        try {
            return compteDAO.findAll();
        }catch (SQLException e){
            throw new SQLException("Failed to find all comptes: " + e.getMessage());
        }
    }
    public List<Compte> findByClient(Client client) throws SQLException {
        try {
            return compteDAO.findByClient(client).get();
        }catch (SQLException e){
            throw new SQLException("Failed to find all comptes: " + e.getMessage());
        }
    }
    public Compte findByid(String id) throws SQLException {
        try {
            return compteDAO.findById(id).get();
        }catch (SQLException e){
            throw new SQLException("Failed to find compte by id: " + e.getMessage());
        }
    }

    public void setCompteDAO(CompteDAO compteDAO) {
        this.compteDAO = compteDAO;
    }

    public CompteDAO getCompteDAO() {
        return compteDAO;
    }

}
