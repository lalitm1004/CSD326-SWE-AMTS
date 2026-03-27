package org.amts.domain.entities.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public class ComplementaryBooking extends Booking {

    private final UUID createdByUserId;

    public ComplementaryBooking(
            UUID id,
            UUID showId,
            String code,
            Money amount,
            LocalDateTime createdAt,
            UUID createdByUserId) {

        super(id, showId, code, BookingType.COMPLEMENTARY, amount, createdAt);
        this.createdByUserId = createdByUserId; // nullable — ON DELETE SET NULL
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }
}