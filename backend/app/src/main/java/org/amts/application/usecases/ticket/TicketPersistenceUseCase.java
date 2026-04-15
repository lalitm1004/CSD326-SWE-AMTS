package org.amts.application.usecases.ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.booking.SpectatorBookingSummary;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;

public interface TicketPersistenceUseCase {

    Optional<Ticket> getTicketById(UUID ticketId);

    List<Ticket> getTicketsByBookingId(UUID bookingId);

    List<SpectatorBookingSummary> getBookingsBySpectatorUserId(UUID spectatorUserId);

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

    UUID getAgentForTicket(UUID ticketId);

    void saveRefundAndMarkTicketWithSeatRelease(UUID ticketId, TicketRefund refund, UUID agentUserId);

    // Kept for standalone use if needed
    void markTicketAsRefunded(UUID ticketId);

    void saveTicketRefund(TicketRefund refund);
}
