package org.amts.domain.entities.finance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public class ConsolidatedYearlyBalanceSheet {

    private final UUID id;
    private final int year;
    private final List<Expense> expenses;
    private final int numberOfShows;
    private final LocalDateTime generatedAt;

    public ConsolidatedYearlyBalanceSheet(
            UUID id,
            int year,
            List<Expense> expenses,
            int numberOfShows,
            LocalDateTime generatedAt) {

        this.id = Objects.requireNonNull(id, "id must not be null");
        this.year = year;
        this.expenses = new ArrayList<>(
                Objects.requireNonNull(expenses, "expenses must not be null"));
        this.numberOfShows = numberOfShows;
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getNumberOfShows() {
        return numberOfShows;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    public Money getTotalExpenses() {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(Money.zero(), Money::add);
    }
}
