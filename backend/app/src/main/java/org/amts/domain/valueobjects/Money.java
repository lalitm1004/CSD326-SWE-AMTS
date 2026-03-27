package org.amts.domain.valueobjects;

import java.util.Objects;

public final class Money implements Comparable<Money> {

    private final double amount;

    private Money(double amount) {
        this.amount = amount;
    }

    public static Money of(double amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(0.0);
    }

    public double getAmount() {
        return amount;
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null Money");
        return new Money(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null Money");
        return new Money(this.amount - other.amount);
    }

    public Money multiply(double factor) {
        return new Money(this.amount * factor);
    }

    /**
     * Returns the given percentage of this amount.
     * e.g., {@code Money.of(100).percentage(10)} returns Money.of(10).
     */
    public Money percentage(double percent) {
        return new Money(this.amount * percent / 100.0);
    }

    public boolean isNegative() {
        return amount < 0;
    }

    public boolean isZero() {
        return Double.compare(amount, 0.0) == 0;
    }

    public boolean isPositive() {
        return amount > 0;
    }

    @Override
    public int compareTo(Money other) {
        return Double.compare(this.amount, other.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Money money = (Money) o;
        return Double.compare(money.amount, amount) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(amount);
    }

    @Override
    public String toString() {
        return String.format("Money{%.2f}", amount);
    }
}
