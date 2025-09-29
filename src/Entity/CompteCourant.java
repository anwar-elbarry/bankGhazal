package Entity;

import java.util.UUID;

public final class CompteCourant extends Compte{

    protected double decouvertAutorise;
    public CompteCourant(double solde, UUID idClient,double decouvertAutorise) {
        super(solde, idClient);
        setDecouvertAutorise(decouvertAutorise);
    }

    public double getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(double decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }
}
