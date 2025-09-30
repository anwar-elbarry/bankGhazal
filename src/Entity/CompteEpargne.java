package Entity;

import java.math.BigDecimal;
import java.util.UUID;

public final class CompteEpargne extends Compte {
    protected double tauxInteret;

    public CompteEpargne(UUID id,String numero,BigDecimal solde, UUID idClient, double tauxInteret) {
        super(id,numero,solde, idClient);
        setTauxInteret(tauxInteret);
    }

    public double getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }
}
