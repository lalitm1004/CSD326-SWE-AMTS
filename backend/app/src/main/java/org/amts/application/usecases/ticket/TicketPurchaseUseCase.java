package org.amts.application.usecases.ticket;

import java.util.List;
import java.util.UUID;

import org.amts.domain.entities.ticket.Ticket;

public interface TicketPurchaseUseCase {

    List<Ticket> purchaseTickets(
            UUID spectatorUserId,
            UUID showId,
            List<UUID> seatIds,
            String couponCode
    );

    String purchaseCoupon(UUID spectatorUserId, UUID showId);

    List<Ticket> purchaseTicketsViaAgent(
            UUID agentUserId,
            UUID spectatorUserId,
            UUID showId,
            List<UUID> seatIds
    );

    double cancelTickets(
            UUID spectatorUserId,
            List<UUID> ticketIds
    );
}