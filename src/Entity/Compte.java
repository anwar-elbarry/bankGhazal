package Entity;

import java.util.UUID;

public sealed class Compte permits CompteEpargne,CompteCourant {
    protected UUID id;
    protected String numero;
    protected double solde;
    protected UUID idClient;
    public Compte(double solde, UUID idClient) {
        setSolde(solde);
        setIdClient(idClient);
        setId(UUID.randomUUID());
        setNumero("CP-"+id.toString().substring(0, 6));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
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
}
