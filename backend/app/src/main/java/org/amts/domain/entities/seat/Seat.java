package org.amts.domain.entities.seat;

import java.util.Objects;
import java.util.UUID;

public class Seat {

    private final UUID id;
    private final String number;
    private final SeatType type;

    public Seat(UUID id, String number, SeatType type) {
        this.id = Objects.requireNonNull(id, "Seat id must not be null");
        this.type = Objects.requireNonNull(type, "Seat type must not be null");

        Objects.requireNonNull(number, "Seat number must not be null");
        if (number.isBlank()) {
            throw new IllegalArgumentException("Seat number must not be blank");
        }
        this.number = number;
    }

    public UUID getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public SeatType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Seat seat = (Seat) o;
        return Objects.equals(id, seat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Seat{id=" + id + ", number='" + number + "', type=" + type + "}";
    }
}
