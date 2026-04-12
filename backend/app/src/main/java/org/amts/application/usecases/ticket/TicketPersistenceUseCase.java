package org.amts.application.usecases.ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;

public interface TicketPersistenceUseCase {

    Optional<Ticket> getTicketById(UUID ticketId);

    List<Ticket> getTicketsByBookingId(UUID bookingId);

    UUID fetchCouponId(UUID spectatorUserId, String couponCode);

    void saveOnlineBookingWithTickets(
            UUID bookingId,
            UUID showId,
            UUID spectatorUserId,
            UUID couponId,
            double total,
            List<Ticket> tickets
    );

    void saveOfflineBookingWithTickets(
            UUID bookingId,
            UUID showId,
            UUID spectatorUserId,
            UUID agentUserId,
            double total,
            List<Ticket> tickets
    );

    String saveCoupon(UUID spectatorUserId, UUID showId);

    void saveTickets(List<Ticket> tickets);

    void saveRefundAndMarkTicket(UUID ticketId, TicketRefund refund);

    // Kept for standalone use if needed
    void markTicketAsRefunded(UUID ticketId);

    void saveTicketRefund(TicketRefund refund);
}