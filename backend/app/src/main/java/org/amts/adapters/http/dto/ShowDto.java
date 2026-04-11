package org.amts.adapters.http.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShowDto {

    private final UUID id;
    private final UUID eventId;
    private final UUID createdByUserId;

    private final String name;
    private final String description;
    private final String thumbnailUrl;

    private final LocalDateTime startingAt;
    private final LocalDateTime endingAt;
    private final LocalDateTime createdAt;

    private final double ordinarySeatPrice;
    private final double balconySeatPrice;
    private final int numOrdinarySeats;
    private final int numBalconySeats;

    public ShowDto(
            UUID id,
            UUID eventId,
            UUID createdByUserId,
            String name,
            String description,
            String thumbnailUrl,
            LocalDateTime startingAt,
            LocalDateTime endingAt,
            LocalDateTime createdAt,
            double ordinarySeatPrice,
            double balconySeatPrice,
            int numOrdinarySeats,
            int numBalconySeats) {

        this.id = id;
        this.eventId = eventId;
        this.createdByUserId = createdByUserId;
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.startingAt = startingAt;
        this.endingAt = endingAt;
        this.createdAt = createdAt;
        this.ordinarySeatPrice = ordinarySeatPrice;
        this.balconySeatPrice = balconySeatPrice;
        this.numOrdinarySeats = numOrdinarySeats;
        this.numBalconySeats = numBalconySeats;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getOrdinarySeatPrice() {
        return ordinarySeatPrice;
    }

    public double getBalconySeatPrice() {
        return balconySeatPrice;
    }

    public int getNumOrdinarySeats() {
        return numOrdinarySeats;
    }

    public int getNumBalconySeats() {
        return numBalconySeats;
    }
}