package Entity;

import java.math.BigDecimal;
import java.util.UUID;

public final class CompteCourant extends Compte{

    protected double decouvertAutorise;
    public CompteCourant(UUID id, String numero,BigDecimal solde, UUID idClient, double decouvertAutorise) {
        super(id,numero,solde,idClient);
        setDecouvertAutorise(decouvertAutorise);
    }

    public double getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(double decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }
}
