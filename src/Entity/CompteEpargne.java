package Entity;

import java.util.UUID;

public final class CompteEpargne extends Compte {
    protected double tauxInteret;

    public CompteEpargne(double solde, UUID idClient, double tauxInteret) {
        super(solde, idClient);
        setTauxInteret(tauxInteret);
    }

    public double getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }
}
