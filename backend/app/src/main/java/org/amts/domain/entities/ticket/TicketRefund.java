package org.amts.domain.entities.ticket;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TicketRefund {

    private final UUID ticketId;
    private final RefundType type;
    private final LocalDateTime createdAt;

    public TicketRefund(UUID ticketId, RefundType type, LocalDateTime createdAt) {
        this.ticketId = Objects.requireNonNull(ticketId, "TicketRefund ticketId must not be null");
        this.type = Objects.requireNonNull(type, "TicketRefund type must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "TicketRefund createdAt must not be null");
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public RefundType getType() {
        return type;
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
        TicketRefund that = (TicketRefund) o;
        return Objects.equals(ticketId, that.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    @Override
    public String toString() {
        return "TicketRefund{ticketId=" + ticketId + ", type=" + type + "}";
    }
}
