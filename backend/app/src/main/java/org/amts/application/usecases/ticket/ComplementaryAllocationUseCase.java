package org.amts.application.usecases.ticket;

import java.util.List;
import java.util.UUID;

import org.amts.domain.entities.ticket.Ticket;

public interface ComplementaryAllocationUseCase {

    List<Ticket> allocateComplementaryTickets(
            UUID allocatedByUserId,
            UUID showId,
            List<UUID> seatIds
    );
}
