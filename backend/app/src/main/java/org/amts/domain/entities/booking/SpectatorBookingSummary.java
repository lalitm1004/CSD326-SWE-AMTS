package org.amts.domain.entities.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import org.amts.domain.valueobjects.Money;

public class SpectatorBookingSummary extends Booking {

    private final int ticketCount;
    private final int refundedTicketCount;

    public SpectatorBookingSummary(
            UUID id,
            UUID showId,
            String code,
            BookingType type,
            Money amount,
            LocalDateTime createdAt,
            int ticketCount,
            int refundedTicketCount) {

        super(id, showId, code, type, amount, createdAt);
        this.ticketCount = ticketCount;
        this.refundedTicketCount = refundedTicketCount;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public int getRefundedTicketCount() {
        return refundedTicketCount;
    }
}
