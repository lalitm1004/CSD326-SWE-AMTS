package com.amts.events;

import java.time.LocalDateTime;
import java.util.UUID;

public class Show {

    private UUID id;
    private UUID eventId;
    private UUID createdByUserId;

    private String name;
    private String description;
    private String thumbnailUrl;

    private LocalDateTime startingAt;
    private LocalDateTime endingAt;

    private double ordinarySeatPrice;
    private double balconySeatPrice;

    private int numOrdinarySeats;
    private int numBalconySeats;

    private LocalDateTime createdAt;

    public Show(
      UUID id,
      UUID eventId,
      UUID createdByUserId,
      String name,
      String description,
      String thumbnailUrl,
      LocalDateTime startingAt,
      LocalDateTime endingAt,
      double ordinarySeatPrice,
      double balconySeatPrice,
      int numOrdinarySeats,
      int numBalconySeats,
      LocalDateTime createdAt
    ) {

        this.id = id;
        this.eventId = eventId;
        this.createdByUserId = createdByUserId;
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.startingAt = startingAt;
        this.endingAt = endingAt;
        this.ordinarySeatPrice = ordinarySeatPrice;
        this.balconySeatPrice = balconySeatPrice;
        this.numOrdinarySeats = numOrdinarySeats;
        this.numBalconySeats = numBalconySeats;
        this.createdAt = createdAt;
    }

    // getters
    public UUID getId() { return id; }
    public UUID getEventId() { return eventId; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public LocalDateTime getStartingAt() { return startingAt; }
    public LocalDateTime getEndingAt() { return endingAt; }
    public double getOrdinarySeatPrice() { return ordinarySeatPrice; }
    public double getBalconySeatPrice() { return balconySeatPrice; }
    public int getNumOrdinarySeats() { return numOrdinarySeats; }
    public int getNumBalconySeats() { return numBalconySeats; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setStartingAt(LocalDateTime startingAt) { this.startingAt = startingAt; }
    public void setEndingAt(LocalDateTime endingAt) { this.endingAt = endingAt; }
    public void setOrdinarySeatPrice(double price) { this.ordinarySeatPrice = price; }
    public void setBalconySeatPrice(double price) { this.balconySeatPrice = price; }
    public void setNumOrdinarySeats(int n) { this.numOrdinarySeats = n; }
    public void setNumBalconySeats(int n) { this.numBalconySeats = n; }
}
