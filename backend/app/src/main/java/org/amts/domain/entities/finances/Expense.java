package org.amts.domain.entities.finances;

import java.time.LocalDateTime;
import java.util.UUID;

public class Expense {

    private UUID id;
    private UUID financialClerkId;
    private UUID balanceSheetId;
    private LocalDateTime createdAt;

    public Expense(
            UUID id,
            UUID financialClerkId,
            UUID balanceSheetId,
            LocalDateTime createdAt) {

        this.id = id;
        this.financialClerkId = financialClerkId;
        this.balanceSheetId = balanceSheetId;
        this.createdAt = createdAt;
    }

    // getters
    public UUID getId() {
        return id;
    }

    public UUID getFinancialClerkId() {
        return financialClerkId;
    }

    public UUID getBalanceSheetId() {
        return balanceSheetId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
