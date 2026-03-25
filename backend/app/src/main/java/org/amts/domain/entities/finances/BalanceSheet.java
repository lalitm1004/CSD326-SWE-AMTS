package org.amts.domain.entities.finances;

import java.time.LocalDateTime;
import java.util.UUID;

public class BalanceSheet {

    private UUID id;
    private UUID financialClerkId;
    private UUID showId;
    private LocalDateTime createdAt;

    public BalanceSheet(
            UUID id,
            UUID financialClerkId,
            UUID showId,
            LocalDateTime createdAt) {
        this.id = id;
        this.financialClerkId = financialClerkId;
        this.showId = showId;
        this.createdAt = createdAt;
    }

    // getters
    public UUID getId() {
        return id;
    }

    public UUID getFinancialClerkId() {
        return financialClerkId;
    }

    public UUID getShowId() {
        return showId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
