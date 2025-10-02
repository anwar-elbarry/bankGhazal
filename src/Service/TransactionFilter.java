package Service;

import Entity.Enum.TypeTransaction;

import java.time.LocalDateTime;

public class TransactionFilter {
    private Double minMontant;
    private Double maxMontant;
    private TypeTransaction type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String lieu;

    public Double getMinMontant() {
        return minMontant;
    }

    public void setMinMontant(Double minMontant) {
        this.minMontant = minMontant;
    }

    public Double getMaxMontant() {
        return maxMontant;
    }

    public void setMaxMontant(Double maxMontant) {
        this.maxMontant = maxMontant;
    }

    public TypeTransaction getType() {
        return type;
    }

    public void setType(TypeTransaction type) {
        this.type = type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
}
