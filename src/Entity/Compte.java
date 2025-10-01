package Entity;

import java.math.BigDecimal;
import java.util.UUID;

public sealed class Compte permits CompteEpargne,CompteCourant {
    protected UUID id;
    protected String numero;
    protected BigDecimal solde;
    protected UUID idClient;
    protected TypeCompte typeCompte;
    public Compte(UUID id, String numero, BigDecimal solde, UUID idClient,TypeCompte typeCompte) {
        setId(id);
        setNumero(numero);
        setSolde(solde);
        setIdClient(idClient);
        setTypeCompte(typeCompte);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public UUID getIdClient() {
        return idClient;
    }

    public void setIdClient(UUID idClient) {
        this.idClient = idClient;
    }
    public TypeCompte getTypeCompte() {
        return typeCompte;
    }
    public void setTypeCompte(TypeCompte typeCompte) {
        this.typeCompte = typeCompte;
    }
}
