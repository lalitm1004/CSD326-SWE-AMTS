package org.amts.application.usecases.ticket;

import java.util.List;
import java.util.UUID;

import org.amts.adapters.usecases.AuthorizationHelper;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.booking.SpectatorBookingSummary;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.user.Role;

public class TicketUseCase {

    private final TicketPersistenceUseCase persistence;
    private final TicketPurchaseUseCase purchaseUseCase;
    private final UserPersistenceUseCase userPersistence;

    public TicketUseCase(
            TicketPersistenceUseCase persistence,
            TicketPurchaseUseCase purchaseUseCase,
            UserPersistenceUseCase userPersistence) {

        this.persistence = persistence;
        this.purchaseUseCase = purchaseUseCase;
        this.userPersistence = userPersistence;
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

    public List<SpectatorBookingSummary> getBookingsBySpectatorUserId(UUID spectatorUserId) {
        AuthorizationHelper.getAuthorizedUser(spectatorUserId, userPersistence, Role.SPECTATOR);
        return persistence.getBookingsBySpectatorUserId(spectatorUserId);
    }
}
