package Service;

import DAO.CompteDAO;
import Entity.Compte;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompteService {
    private CompteDAO compteDAO;
    private Map<UUID,Compte> comptes = new HashMap<>();
    public CompteService(CompteDAO compteDAO){
        setCompteDAO(compteDAO);
    }

    public void addCompte(Compte compte) throws SQLException {
           if (compte.getSolde().compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException("Solde must be greater than or equal to zero");
           }
           try{
           UUID id = UUID.randomUUID();
           String numero = "CP" + id.toString().substring(0, 6);
           compte.setId(id);
           compte.setNumero(numero);
           compteDAO.addCompte(compte);
           comptes.put(id,compte);
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
            comptes.put(compte.getId(),compte);
        }catch (SQLException e){
            throw new SQLException("Failed to modify compte: " + e.getMessage());
        }
    }

    public void removeCompte(Compte compte) throws SQLException {
        try{
            compteDAO.removeCompte(compte);
            comptes.remove(compte.getId());
        }catch (SQLException e){
            throw new SQLException("Failed to remove compte: " + e.getMessage());
        }
    }

    public void setCompteDAO(CompteDAO compteDAO) {
        this.compteDAO = compteDAO;
    }

    public CompteDAO getCompteDAO() {
        return compteDAO;
    }

    public Map<UUID, Compte> getComptes() {
        return comptes;
    }
}
