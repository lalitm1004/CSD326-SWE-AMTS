package org.amts.domain.entities.finance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public class BalanceSheet {

    private final UUID id;
    private final UUID financialClerkUserId;
    private final UUID showId;

    private final List<Expense> expenses;

    private final LocalDateTime createdAt;

    public BalanceSheet(
            UUID id,
            UUID financialClerkUserId,
            UUID showId,
            List<Expense> expenses,
            LocalDateTime createdAt) {

        this.id = Objects.requireNonNull(id, "BalanceSheet id must not be null");
        this.financialClerkUserId = financialClerkUserId; // nullable — ON DELETE SET NULL
        this.showId = showId; // nullable — ON DELETE SET NULL

        this.expenses = new ArrayList<>(
                Objects.requireNonNull(expenses, "BalanceSheet expenses must not be null"));

        this.createdAt = Objects.requireNonNull(createdAt, "BalanceSheet createdAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public UUID getFinancialClerkUserId() {
        return financialClerkUserId;
    }

    public UUID getShowId() {
        return showId;
    }

    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Money getTotalExpenses() {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(Money.zero(), Money::add);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BalanceSheet that = (BalanceSheet) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BalanceSheet{id=" + id + ", showId=" + showId + ", expenseCount=" + expenses.size() + "}";
    }
}
