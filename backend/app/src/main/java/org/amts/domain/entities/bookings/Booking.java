package org.amts.domain.entities.bookings;

import java.time.LocalDateTime;
import java.util.UUID;

public class Booking {

    private UUID id;
    private UUID showId;
    private UUID spectatorUserId;
    private UUID salesAgentId; // nullable

    private LocalDateTime createdAt;

    public Booking(
            UUID id,
            UUID showId,
            UUID spectatorUserId,
            UUID salesAgentId,
            LocalDateTime createdAt) {

        this.id = id;
        this.showId = showId;
        this.spectatorUserId = spectatorUserId;
        this.salesAgentId = salesAgentId;
        this.createdAt = createdAt;
    }

    // getters
    public UUID getId() {
        return id;
    }

    public UUID getShowId() {
        return showId;
    }

    public UUID getSpectatorUserId() {
        return spectatorUserId;
    }

    public UUID getSalesAgentId() {
        return salesAgentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // setters
    public void setSalesAgentId(UUID salesAgentId) {
        this.salesAgentId = salesAgentId;
    }
}
