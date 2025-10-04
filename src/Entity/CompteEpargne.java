package Entity;

import Entity.Enum.TypeCompte;

import java.math.BigDecimal;

public final class CompteEpargne extends Compte {
    protected double tauxInteret;

    public CompteEpargne(String id,String numero,BigDecimal solde, String idClient, double tauxInteret) {
        super(id,numero,solde, idClient, TypeCompte.EPARGNE);
        setTauxInteret(tauxInteret);
    }

    public double getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }
}
