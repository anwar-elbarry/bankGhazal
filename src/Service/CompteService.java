package Service;

import DAO.CompteDAO;
import Entity.*;
import Entity.Enum.TypeCompte;

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
               String id =UUID.randomUUID().toString();
               String numero = "CP" + id.substring(0, 6);
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
            } else if (compte instanceof CompteCourant && ((CompteCourant)compte).getDecouvertAutorise() < 0){
                throw new IllegalArgumentException("Decouvert autorise must be greater than or equal to zero");
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
    public Compte findByNumero(String numero) throws SQLException {
        try {
            return compteDAO.findByNumero(numero).get();
        }catch (SQLException e){
            throw new SQLException("Failed to find compte by numero: " + e.getMessage());
        }
    }

    public Optional<Compte> maxOUminSolde(String maxOUmin) throws SQLException {
        try{
            List<Compte> comptes = compteDAO.findAll();
            Compte compte;
        if (Objects.equals(maxOUmin, "MAX")){
            compte = comptes.stream().max(Comparator.comparing(Compte::getSolde)).get();
            return Optional.of(compte);
        }else if (Objects.equals(maxOUmin, "MIN")){
            compte = comptes.stream().min(Comparator.comparing(Compte::getSolde)).get();
            return Optional.of(compte);
        }
        }catch (SQLException e){
            throw new SQLException("Failed to find max or min solde: " + e.getMessage());
        }
      return Optional.empty();
    }


    public void versement(Compte compte, BigDecimal montant, String lieu) throws SQLException {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }
        if (compte == null) {
            throw new IllegalArgumentException("Le compte ne peut pas être null");
        }

        try {
            BigDecimal nouveauSolde = compte.getSolde().add(montant);
            compte.setSolde(nouveauSolde);
            compteDAO.modifyCompte(compte);

            System.out.println("Versement réussi: " + montant + " ajouté au compte " + compte.getNumero());
            System.out.println("Nouveau solde: " + nouveauSolde);
        } catch (SQLException e) {
            throw new SQLException("Échec du versement: " + e.getMessage());
        }
    }


    public void retrait(Compte compte, BigDecimal montant, String lieu) throws SQLException {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }
        if (compte == null) {
            throw new IllegalArgumentException("Le compte ne peut pas être null");
        }

        try {
            BigDecimal nouveauSolde = compte.getSolde().subtract(montant);

            if (compte instanceof CompteCourant) {
                CompteCourant compteCourant = (CompteCourant) compte;
                BigDecimal limiteDecouvert = BigDecimal.valueOf(-compteCourant.getDecouvertAutorise());

                if (nouveauSolde.compareTo(limiteDecouvert) < 0) {
                    throw new IllegalArgumentException(
                            "Solde insuffisant. Découvert autorisé: " + compteCourant.getDecouvertAutorise() +
                                    ". Solde après retrait serait: " + nouveauSolde
                    );
                }
            } else {
                if (nouveauSolde.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(
                            "Solde insuffisant. Solde actuel: " + compte.getSolde() +
                                    ". Montant demandé: " + montant
                    );
                }
            }

            compte.setSolde(nouveauSolde);
            compteDAO.modifyCompte(compte);

            System.out.println("Retrait réussi: " + montant + " retiré du compte " + compte.getNumero());
            System.out.println("Nouveau solde: " + nouveauSolde);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            throw new SQLException("Échec du retrait: " + e.getMessage());
        }
    }


    public void virement(Compte compteSource, Compte compteDestination, BigDecimal montant, String lieu) throws SQLException {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }
        if (compteSource == null || compteDestination == null) {
            throw new IllegalArgumentException("Les comptes ne peuvent pas être null");
        }
        if (compteSource.getId().equals(compteDestination.getId())) {
            throw new IllegalArgumentException("Le compte source et le compte destination doivent être différents");
        }

        try {
            BigDecimal nouveauSoldeSource = compteSource.getSolde().subtract(montant);

            if (compteSource instanceof CompteCourant) {
                CompteCourant compteCourant = (CompteCourant) compteSource;
                BigDecimal limiteDecouvert = BigDecimal.valueOf(-compteCourant.getDecouvertAutorise());

                if (nouveauSoldeSource.compareTo(limiteDecouvert) < 0) {
                    throw new IllegalArgumentException(
                            "Solde insuffisant pour le virement. Découvert autorisé: " + compteCourant.getDecouvertAutorise() +
                                    ". Solde après virement serait: " + nouveauSoldeSource
                    );
                }
            } else {
                if (nouveauSoldeSource.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(
                            "Solde insuffisant pour le virement. Solde actuel: " + compteSource.getSolde() +
                                    ". Montant demandé: " + montant
                    );
                }
            }

            // Perform the transfer
            BigDecimal nouveauSoldeDestination = compteDestination.getSolde().add(montant);

            compteSource.setSolde(nouveauSoldeSource);
            compteDestination.setSolde(nouveauSoldeDestination);

            compteDAO.modifyCompte(compteSource);
            compteDAO.modifyCompte(compteDestination);

            System.out.println("Virement réussi: " + montant + " transféré de " + compteSource.getNumero() +
                    " vers " + compteDestination.getNumero());
            System.out.println("Nouveau solde source: " + nouveauSoldeSource);
            System.out.println("Nouveau solde destination: " + nouveauSoldeDestination);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            throw new SQLException("Échec du virement: " + e.getMessage());
        }
    }

    public void setCompteDAO(CompteDAO compteDAO) {
        this.compteDAO = compteDAO;
    }

    public CompteDAO getCompteDAO() {
        return compteDAO;
    }

}
