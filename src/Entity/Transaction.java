package Entity;

import Entity.Enum.TypeTransaction;

import java.lang.String;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(String id , BigDecimal montant , LocalDateTime date , String lieu , TypeTransaction type, String idCompte) {
}
