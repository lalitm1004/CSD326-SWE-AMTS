package org.amts.domain.entities.booking;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public abstract class Booking {

    protected final UUID id;
    protected final UUID showId;

    protected final String code;
    protected final BookingType type;
    protected final Money amount;

    protected final LocalDateTime createdAt;

    protected Booking(
            UUID id,
            UUID showId,
            String code,
            BookingType type,
            Money amount,
            LocalDateTime createdAt) {

        this.id = Objects.requireNonNull(id, "Booking id must not be null");
        this.showId = showId; // nullable — ON DELETE SET NULL in DB

        Objects.requireNonNull(code, "Booking code must not be null");
        if (code.isBlank()) {
            throw new IllegalArgumentException("Booking code must not be blank");
        }
        this.code = code;

        this.type = Objects.requireNonNull(type, "Booking type must not be null");
        this.amount = amount; // nullable — "Updated via transactions" in DB
        this.createdAt = Objects.requireNonNull(createdAt, "Booking createdAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public UUID getShowId() {
        return showId;
    }

    public String getCode() {
        return code;
    }

    public BookingType getType() {
        return type;
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
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", type=" + type + "}";
    }
}