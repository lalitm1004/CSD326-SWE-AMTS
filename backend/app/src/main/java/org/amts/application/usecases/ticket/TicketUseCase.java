package org.amts.application.usecases.ticket;

import java.util.List;
import java.util.UUID;

import org.amts.domain.entities.ticket.Ticket;

public class TicketUseCase {

    private final TicketPersistenceUseCase persistence;
    private final TicketPurchaseUseCase purchaseUseCase;

    public TicketUseCase(
            TicketPersistenceUseCase persistence,
            TicketPurchaseUseCase purchaseUseCase) {

        this.persistence = persistence;
        this.purchaseUseCase = purchaseUseCase;
    }

    public List<Ticket> purchaseTickets(
            UUID spectatorUserId,
            UUID showId,
            List<UUID> seatIds,
            String couponCode) {

        return purchaseUseCase.purchaseTickets(
                spectatorUserId,
                showId,
                seatIds,
                couponCode
        );
    }

    public String purchaseCoupon(UUID spectatorUserId, UUID showId) {
        return purchaseUseCase.purchaseCoupon(spectatorUserId, showId);
    }

    public List<Ticket> purchaseTicketsViaAgent(
            UUID agentUserId,
            UUID spectatorUserId,
            UUID showId,
            List<UUID> seatIds) {

        return purchaseUseCase.purchaseTicketsViaAgent(
                agentUserId,
                spectatorUserId,
                showId,
                seatIds
        );
    }

    public double cancelTickets(
            UUID spectatorUserId,
            List<UUID> ticketIds) {

        return purchaseUseCase.cancelTickets(
                spectatorUserId,
                ticketIds
        );
    }

    public List<Ticket> getTicketsByBookingId(UUID bookingId) {
        return persistence.getTicketsByBookingId(bookingId);
    }
}