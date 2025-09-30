package Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(UUID id , BigDecimal montant , LocalDateTime date , String lieu , TypeTransaction type, UUID idCompte) {
}
