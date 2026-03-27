package org.amts.domain.entities.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public class OfflineBooking extends Booking {

    private final UUID spectatorUserId;
    private final UUID salesAgentUserId;

    public OfflineBooking(
            UUID id,
            UUID showId,
            String code,
            Money amount,
            LocalDateTime createdAt,
            UUID spectatorUserId,
            UUID salesAgentUserId) {

        super(id, showId, code, BookingType.OFFLINE, amount, createdAt);
        this.spectatorUserId = spectatorUserId; // nullable — ON DELETE SET NULL
        this.salesAgentUserId = salesAgentUserId; // nullable — ON DELETE SET NULL
    }

    public UUID getSpectatorUserId() {
        return spectatorUserId;
    }

    public UUID getSalesAgentUserId() {
        return salesAgentUserId;
    }
}
