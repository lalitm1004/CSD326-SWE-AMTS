package org.amts.domain.entities.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public class OnlineBooking extends Booking {

    private final UUID spectatorUserId;
    private final UUID couponId;

    public OnlineBooking(
            UUID id,
            UUID showId,
            String code,
            Money amount,
            LocalDateTime createdAt,
            UUID spectatorUserId,
            UUID couponId) {

        super(id, showId, code, BookingType.ONLINE, amount, createdAt);
        this.spectatorUserId = spectatorUserId; // nullable — ON DELETE SET NULL
        this.couponId = couponId; // nullable — coupon is optional
    }

    public UUID getSpectatorUserId() {
        return spectatorUserId;
    }

    public UUID getCouponId() {
        return couponId;
    }

    public boolean hasCoupon() {
        return couponId != null;
    }
}