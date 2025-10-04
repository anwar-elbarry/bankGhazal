package Entity;

import Entity.Enum.TypeCompte;

import java.lang.String;
import java.math.BigDecimal;

public sealed class Compte permits CompteEpargne,CompteCourant {
    protected String id;
    protected String numero;
    protected BigDecimal solde;
    protected String idClient;
    protected TypeCompte typeCompte;
    public Compte(String id, String numero, BigDecimal solde, String idClient, TypeCompte typeCompte) {
        setId(id);
        setNumero(numero);
        setSolde(solde);
        setIdClient(idClient);
        setTypeCompte(typeCompte);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }
    public TypeCompte getTypeCompte() {
        return typeCompte;
    }
    public void setTypeCompte(TypeCompte typeCompte) {
        this.typeCompte = typeCompte;
    }

    @Override
    public String toString() {
        return "Compte{ " +
                "numero = " + numero +
                ", solde = " + solde +
                ", idClient = " + idClient +
                ", typeCompte = " + typeCompte +
                '}';
    }
}
