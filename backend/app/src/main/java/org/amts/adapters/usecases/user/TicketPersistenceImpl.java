package org.amts.adapters.usecase.ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;

public class TicketPersistenceImpl implements TicketPersistenceUseCase {

    private final Map<UUID, Ticket> ticketStore = new HashMap<>();
    private final Map<UUID, List<Ticket>> bookingMap = new HashMap<>();
    private final Map<UUID, TicketRefund> refundStore = new HashMap<>();

    @Override
    public Optional<Ticket> getTicketById(UUID ticketId) {
        return Optional.ofNullable(ticketStore.get(ticketId));
    }

    @Override
    public List<Ticket> getTicketsByBookingId(UUID bookingId) {
        return bookingMap.getOrDefault(bookingId, new ArrayList<>());
    }

    @Override
    public void saveTickets(List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            ticketStore.put(ticket.getId(), ticket);

            bookingMap
                .computeIfAbsent(ticket.getBookingId(), k -> new ArrayList<>())
                .add(ticket);
        }
    }

    @Override
    public void markTicketAsRefunded(UUID ticketId) {
        Ticket ticket = ticketStore.get(ticketId);
        if (ticket == null) return;

        Ticket updated = new Ticket(
                ticket.getId(),
                ticket.getBookingId(),
                ticket.getShowId(),
                ticket.getSeatId(),
                ticket.getCode(),
                true,
                ticket.getCreatedAt()
        );

        ticketStore.put(ticketId, updated);
    }

    @Override
    public void saveTicketRefund(TicketRefund refund) {
        refundStore.put(refund.getTicketId(), refund);
    }
}