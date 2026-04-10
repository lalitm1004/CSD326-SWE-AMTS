package org.amts.application.usecases.ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.ticket.Ticket;
import org.amts.domain.entities.ticket.TicketRefund;

public interface TicketPersistenceUseCase {

    Optional<Ticket> getTicketById(UUID ticketId);

    List<Ticket> getTicketsByBookingId(UUID bookingId);

    void saveTickets(List<Ticket> tickets);

    void markTicketAsRefunded(UUID ticketId);

    void saveTicketRefund(TicketRefund refund);
}