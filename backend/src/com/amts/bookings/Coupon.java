package com.amts.bookings;

import java.util.UUID;

public class Coupon {

    private UUID showId;
    private UUID userId;

    public Coupon(UUID showId, UUID userId) {
        this.showId = showId;
        this.userId = userId;
    }

    // getters
    public UUID getShowId() { return showId; }
    public UUID getUserId() { return userId; }
}
