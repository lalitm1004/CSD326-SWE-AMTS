package org.amts.domain.entities.ticket;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Ticket {

    private final UUID id;
    private final UUID bookingId;
    private final UUID showId;
    private final UUID seatId;

    private final String code;
    private final boolean refunded;

    private final LocalDateTime createdAt;

    public Ticket(
            UUID id,
            UUID bookingId,
            UUID showId,
            UUID seatId,
            String code,
            boolean refunded,
            LocalDateTime createdAt) {

        this.id = Objects.requireNonNull(id, "Ticket id must not be null");
        this.bookingId = bookingId; // nullable — ON DELETE CASCADE but may be null in edge cases
        this.showId = showId; // nullable — ON DELETE CASCADE
        this.seatId = seatId; // nullable — ON DELETE SET NULL

        Objects.requireNonNull(code, "Ticket code must not be null");
        if (code.isBlank()) {
            throw new IllegalArgumentException("Ticket code must not be blank");
        }
        this.code = code;

        this.refunded = refunded;
        this.createdAt = Objects.requireNonNull(createdAt, "Ticket createdAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getShowId() {
        return showId;
    }

    public UUID getSeatId() {
        return seatId;
    }

    public String getCode() {
        return code;
    }

    public boolean isRefunded() {
        return refunded;
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
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ticket{id=" + id + ", code='" + code + "', refunded=" + refunded + "}";
    }
}
