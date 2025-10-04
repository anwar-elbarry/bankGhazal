package Entity;

import Entity.Enum.TypeCompte;

import java.math.BigDecimal;


public final class CompteCourant extends Compte{

    protected double decouvertAutorise;
    public CompteCourant(String id, String numero,BigDecimal solde, String idClient, double decouvertAutorise) {
        super(id,numero,solde,idClient, TypeCompte.COURANT);
        setDecouvertAutorise(decouvertAutorise);
    }

    public double getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(double decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }
}
