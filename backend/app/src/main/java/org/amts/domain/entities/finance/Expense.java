package org.amts.domain.entities.finance;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public class Expense {

    private final UUID id;
    private final UUID financialClerkUserId;
    private final UUID balanceSheetId;

    private final String name;
    private final String description;
    private final Money amount;

    private final LocalDateTime createdAt;

    public Expense(
            UUID id,
            UUID financialClerkUserId,
            UUID balanceSheetId,
            String name,
            String description,
            Money amount,
            LocalDateTime createdAt) {

        this.id = Objects.requireNonNull(id, "Expense id must not be null");
        this.financialClerkUserId = financialClerkUserId; // nullable — ON DELETE SET NULL
        this.balanceSheetId = Objects.requireNonNull(balanceSheetId, "Expense balanceSheetId must not be null");

        Objects.requireNonNull(name, "Expense name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Expense name must not be blank");
        }
        this.name = name;
        this.description = description; // nullable

        this.amount = Objects.requireNonNull(amount, "Expense amount must not be null");
        if (amount.isNegative()) {
            throw new IllegalArgumentException("Expense amount must not be negative");
        }

        this.createdAt = Objects.requireNonNull(createdAt, "Expense createdAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public UUID getFinancialClerkUserId() {
        return financialClerkUserId;
    }

    public UUID getBalanceSheetId() {
        return balanceSheetId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Expense{id=" + id + ", name='" + name + "', amount=" + amount + "}";
    }
}
