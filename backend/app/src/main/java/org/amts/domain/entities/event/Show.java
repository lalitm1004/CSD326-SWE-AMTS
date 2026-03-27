package org.amts.domain.entities.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public class Show {

    private final UUID id;
    private final UUID eventId;
    private final UUID createdByUserId;

    private final String name;
    private final String description;
    private final String thumbnailUrl;

    private final LocalDateTime startingAt;
    private final LocalDateTime endingAt;

    private final Money ordinarySeatPrice;
    private final Money balconySeatPrice;
    private final int numOrdinarySeats;
    private final int numBalconySeats;

    private final LocalDateTime createdAt;

    public Show(
            UUID id,
            UUID eventId,
            UUID createdByUserId,
            String name,
            String description,
            String thumbnailUrl,
            LocalDateTime startingAt,
            LocalDateTime endingAt,
            Money ordinarySeatPrice,
            Money balconySeatPrice,
            int numOrdinarySeats,
            int numBalconySeats,
            LocalDateTime createdAt) {

        this.id = Objects.requireNonNull(id, "Show id must not be null");
        this.createdByUserId = createdByUserId; // nullable — ON DELETE SET NULL in DB
        this.eventId = eventId; // nullable — ON DELETE CASCADE but may be orphaned

        Objects.requireNonNull(name, "Show name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Show name must not be blank");
        }
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;

        this.startingAt = Objects.requireNonNull(startingAt, "Show startingAt must not be null");
        this.endingAt = Objects.requireNonNull(endingAt, "Show endingAt must not be null");
        if (!endingAt.isAfter(startingAt)) {
            throw new IllegalArgumentException("Show endingAt must be after startingAt");
        }

        this.ordinarySeatPrice = Objects.requireNonNull(ordinarySeatPrice, "Ordinary seat price must not be null");
        this.balconySeatPrice = Objects.requireNonNull(balconySeatPrice, "Balcony seat price must not be null");
        if (ordinarySeatPrice.isNegative()) {
            throw new IllegalArgumentException("Ordinary seat price must not be negative");
        }
        if (balconySeatPrice.isNegative()) {
            throw new IllegalArgumentException("Balcony seat price must not be negative");
        }

        if (numOrdinarySeats < 0) {
            throw new IllegalArgumentException("Number of ordinary seats must not be negative");
        }
        if (numBalconySeats < 0) {
            throw new IllegalArgumentException("Number of balcony seats must not be negative");
        }
        this.numOrdinarySeats = numOrdinarySeats;
        this.numBalconySeats = numBalconySeats;

        this.createdAt = Objects.requireNonNull(createdAt, "Show createdAt must not be null");
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public LocalDateTime getStartingAt() {
        return startingAt;
    }

    public LocalDateTime getEndingAt() {
        return endingAt;
    }

    public Money getOrdinarySeatPrice() {
        return ordinarySeatPrice;
    }

    public Money getBalconySeatPrice() {
        return balconySeatPrice;
    }

    public int getNumOrdinarySeats() {
        return numOrdinarySeats;
    }

    public int getNumBalconySeats() {
        return numBalconySeats;
    }

    public int getTotalSeats() {
        return numOrdinarySeats + numBalconySeats;
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
        Show show = (Show) o;
        return Objects.equals(id, show.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Show{id=" + id + ", name='" + name + "'}";
    }
}